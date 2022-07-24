import generation.VIST;

/**
 *  A sample use of the VIST compiler *  
 */
public class App {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String test = "highscore IS 25929;";
        VIST vist = new VIST(test);

        int score = vist.retrieveInt("highscore");
        System.out.println(score);    
    }
}