package scripts

import groovy.sql.Sql
import groovy.xml.MarkupBuilder

//contains xml build example and sql example

//external values:
def db = [url       : getBinding().getVariable("datasource"),
          user      : getBinding().getVariable("db_user"),
          password  : getBinding().getVariable("password"),
          driver    : getBinding().getVariable("driver")]

def someVar = getBinding().getVariable("varFromExternalSource")

def UUID = "myUUID"
def errStat = "myErrStat"
def myOperation = "myOperation"
def err_details = "details"

def param1_value = "p1"
def param2_value = "p2"

def sql = null


try {
    sql = Sql.newInstance(db.url, db.user, db.password, db.driver);

    String call_stored_proc_with_params = '''exec <STORED_PROCEDURE_NAME> @Param1 = ?, @Param2 = ?'''
    sql.execute(call_stored_proc_with_params, [param1_value, param2_value])

    def outXml = createXml(sql, errStat, myOperation, err_details)
    //this will be accessible for external source. basically set return value
    getBinding().setVariable("output", outXml)

    String clear_temp_table = '''DELETE <TABLE_TO_BE_CLEARED> WHERE <FIELD_1> like 'out%' and <FIELD_2> = ? and <FIELD_3> = ?'''
    sql.execute(clear_temp_table, [param1_value, param2_value])

} catch (Exception e) {
    println("GROOVY ERR: " + (new Exception(e)).getMessage())
} finally {
    if (sql != null){
        sql.close()
    }
}

String createXml(errStat, myOperation, err_details){
    def stringWriter = new StringWriter()
    def errBuilder = new MarkupBuilder(stringWriter)

    /*
    <?xml version="1.0" encoding="windows-1251"?>
    <cks>
        <return Status="error" UUID="%%UUID_PLACEHOLDER%%">
            <error Status="%%ERR_STATUS_PLACEHOLDER%%" Operation="%%OPERATION_PLACEHOLDER%%">%%ERR_DETAIL_MESSAGE_PLACEHOLDER%%</error>
        </return>
    </cks>
     */


    errBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "windows-1251")
    errBuilder.cks {
        "return"(Status: "error", UUID: UUID){ //quotes because xml element name = java keyword
            error(Status: errStat, Operation: myOperation, err_details){
            }
        }
    }

    getBinding().setVariable("output", stringWriter.toString())


    println(stringWriter.toString())

}


/*
xml container element example

String documentsRequestText = "SELECT docs FROM table WHERE field1 = ? AND field2 = ?"


    documents() {
        // для каждой выбранной строки селекта выполняем
        sql.eachRow(documentsRequestText, [param1, param2]) { doc ->
            // создаем тег документ
            document(){
                type(id: doc?.doc_type_id) <-- value from "doc_type_id" field of documentsRequest query result row will be taken
                name(doc?.name)
                number(doc?.number)
                date(myFunctionForCreateDate(doc?.document_date)) <-- processing fetched value before putting in xml
            } //document
        } //запрос cliDocument
    } //documents

 */
