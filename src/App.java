import syntax.Lexer;

public class App {
    public static void main(String[] args) throws Exception {
        var lexer = new Lexer();

        // TODO: Fix numbers
        final String in = "x IS 6 ;";

        lexer.match(in);
    }
}
