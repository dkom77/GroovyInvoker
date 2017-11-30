import java.io.*;
import java.util.stream.Collectors;

public class ResourceExtractor {
    private final static String CODE_PAGE = "Cp1251";
    private static final String CRLF = "\r\n";

    private File file;
    private String resourceFileName;

    private ResourceExtractor(){
    }

    public static ResourceExtractor getInstance(){
        return new ResourceExtractor();
    }

    public ResourceExtractor setFileSource(File file){
        this.resourceFileName = null;
        this.file = file;
        return this;
    }

    public ResourceExtractor setResourceName(String resourceFileName){
        this.file = null;
        this.resourceFileName = resourceFileName;
        return this;
    }

    public String getContent() throws IOException {
        InputStreamReader streamReader = getResourceReader();
        return new BufferedReader(streamReader).lines().collect(Collectors.joining(CRLF));
    }

    private InputStreamReader getResourceReader() throws IOException{
        if (file != null){
            FileInputStream fileInputStream = new FileInputStream(file);
            return new InputStreamReader(fileInputStream, CODE_PAGE);
        }

        if (resourceFileName != null){
            InputStream resource = getResource(resourceFileName);
            return new InputStreamReader(resource, CODE_PAGE);
        }

        throw new IllegalStateException("Extractor source not set");
    }

    private InputStream getResource(String name) throws IOException{
        /* Springframework approach
            * @see ClassLoader#getResourceAsStream(String)
            * @see Class#getResourceAsStream(String)
        ClassPathResource resource = new ClassPathResource(name);
        return resource.getInputStream();
        */
        //vanilla
        return getClass().getResourceAsStream(name);
    }

}
