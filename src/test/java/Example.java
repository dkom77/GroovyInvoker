public class Example {

    public static void main(String[] args) {
        Invoker invoker = Invoker.getInstance(Invoker.Registered.SCRIPT_ALIAS);

        invoker
                .addParam("datasource", "datasource")
                .addParam("db_user", "user name")
                .addParam("driver", "driver")
                .addParam("password", new Object()) //just for example
                .addParam("varFromExternalSource", "some var");

        String result = (String) invoker.invoke();

    }

}
