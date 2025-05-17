package tum.i2.cma;

import java.util.HashMap;
import java.util.Map;

public enum CMaInstructionType {
    LOADC,
    // Arithmetic and logical (as introduced in Simple expressions and assignments)
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    AND,
    OR,
    XOR,
    // Comparison  (as introduced in Simple expressions and assignments)
    EQ,
    NEQ,
    LE,
    LEQ,
    GR,
    GEQ,
    // Negation (as introduced in Simple expressions and assignments)
    NOT,
    NEG,
    // Assignments
    LOAD,
    STORE,
    LOADA,
    STOREA,
    // Statements (as introduced in Statements and Statement Sequences)
    POP,
    // Conditional and Iterative Statements
    JUMP,
    JUMPZ,
    // Introduced in the Switch Statement
    JUMPI,
    DUP,
    // Introduced in Storage Allocation for Variables
    ALLOC,

    //Week2
    HALT,
    NEW,
    RJUMP,
    RJUMPZ,


    //Week 3
    MARK,
    CALL,
    SLIDE,
    ENTER,
    RETURN,
    LOADRC,
    LOADR,
    STORER,


    ;

    //
    private static final Map<String, CMaInstructionType> STRING_TO_ENUM = new HashMap<>();

    static {
        for (CMaInstructionType type : values()) {
            STRING_TO_ENUM.put(type.name(), type);
        }
    }

    public static CMaInstructionType fromString(String name) {
        CMaInstructionType type = STRING_TO_ENUM.get(name.toUpperCase());
        if (type == null) {
            throw new IllegalArgumentException("Unknown instruction type: " + name);
        }
        return type;
    }

    static int expectedNumberOfArguments(CMaInstructionType type) {
        return switch (type) {
            case LOAD, STORE, POP, LOADC, LOADA, STOREA, JUMP, JUMPZ, JUMPI, ALLOC, RJUMP, RJUMPZ, ENTER, SLIDE, LOADR, STORER -> 1;
            default -> 0;
        };
    }
}
