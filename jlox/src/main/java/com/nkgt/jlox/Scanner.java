package com.nkgt.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nkgt.jlox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }
    
    List<Token> scanTokens() {
        while(!(current >= source.length())) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = source.charAt(current++);

        switch(c) {
            case '(': addToken(LEFT_PAREN, null); break;
            case ')': addToken(RIGHT_PAREN, null); break;
            case '{': addToken(LEFT_BRACE, null); break;
            case '}': addToken(RIGHT_BRACE, null); break;
            case ',': addToken(COMMA, null); break;
            case '.': addToken(DOT, null); break;
            case '-': addToken(MINUS, null); break;
            case '+': addToken(PLUS, null); break;
            case ';': addToken(SEMICOLON, null); break;
            case '*': addToken(STAR, null); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG, null);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL, null);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS, null);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER, null);
                break;
            case '/':
                if(match('/')) {
                    while(peek() != '\n' && !(current >= source.length())) current++;
                } else {
                    addToken(SLASH, null);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if(isDigit(c)) {
                    number();
                } else if(isAlpha(c)) {
                    identifier();  
                } else {
                    Lox.error(line, "Unexpected character.");
                    break;
                }
        }
    }
    
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    
    private boolean match(char expected) {
        if(current >= source.length()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if(current >= source.length()) return '\0';
        return source.charAt(current);
    }
    
    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    private void string() {
        while(peek() != '"' && !(current >= source.length())) {
            if(peek() == '\n') line++;
            current++;
        }
        
        if(current >= source.length()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // Closing '"'
        current++;

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void number() {
        while(isDigit(peek())) current++;

        if(peek() == '.' && isDigit(peekNext())) {
            current++;
            while(isDigit(peek())) current++;
        }
        
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) current++;

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null) type = IDENTIFIER;

        addToken(type, null);
    }
}
