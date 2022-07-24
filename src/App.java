import syntax.Lexer;

public class App {
    public static void main(String[] args) throws Exception {
        var lexer = new Lexer();
        final String in = "x IS 256;";

        lexer.match(in);
    }
}
