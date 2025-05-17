package tum.i2.cma;

import tum.i2.common.VirtualMachine;

import java.util.Objects;
import java.util.function.BiFunction;

public class CMa implements VirtualMachine {

    int stacksize = 1000;
    boolean halted = false;


    int SP = -1;
    int EP = -1;
    int FP = -1;

    int NP = stacksize;
    int[] S = new int[stacksize];

    int PC = 0;
    CMaInstruction[] instructions;

    public CMa(CMaInstruction[] instructions) {
        this.instructions = instructions;
    }

    @Override
    public void step() {
        if (halted) {
            return;
        }

        CMaInstruction instruction = instructions[PC];
        printDebug();
        PC++;
        execute(instruction);
    }

    @Override
    public int run() {
        // TODO Implement the main loop of the VM
        // as introduced in the lecture
        // We have defined the step() method here,
        // because it might make it easier to debug,
        // and would be required if you wish to implement
        // an interface with step function

        while (!halted) {
            step();
        }

        return S[SP];
    }

    private void printDebug() {
        System.out.println("Registers: SP=" + SP + ", EP=" + EP + ", FP=" + FP);
        System.out.print("Stack:\n[ ");
        for (int i = 0; i < SP + 1; i++) {
            System.out.print(S[i] + " ");
        }
        System.out.println("]");

        System.out.print("Heap:     NP=" + NP + "\n[ ");
        for (int i = NP; i < stacksize; i++) {
            System.out.print(S[i] + " ");
        }
        System.out.println("]\n");

        System.out.println(instructions[PC]);
        System.out.println();
    }

    public void executeBinaryIntegerOperator(BiFunction<Integer, Integer, Integer> operator) {
        int i = operator.apply(S[SP - 1], S[SP]);
        S[SP - 1] = i;
        SP--;
    }

    public void executeLogicalOperator(BiFunction<Integer, Integer, Boolean> operator) {
        int b = operator.apply(S[SP - 1], S[SP]) ? 1 : 0;
        S[SP - 1] = b;
        SP--;
    }

    public void execute(CMaInstruction instruction) {
        // CMaInstructionType enum contains comments,
        // describing where the operations are defined
        switch (instruction.getType()) {
            case LOADC -> {
                SP++;
                S[SP] = instruction.getFirstArg();
            }
            case ADD -> executeBinaryIntegerOperator(Math::addExact);
            case SUB -> executeBinaryIntegerOperator(Math::subtractExact);
            case MUL -> executeBinaryIntegerOperator(Math::multiplyExact);
            case DIV -> executeBinaryIntegerOperator(Math::divideExact);
            case MOD -> executeBinaryIntegerOperator(Math::floorMod);
            case AND -> executeLogicalOperator((a, b) -> a > 0 && b > 0);
            case OR -> executeLogicalOperator((a, b) -> a > 0 || b > 0);
            case XOR -> executeLogicalOperator((a, b) -> a > 0 ^ b > 0);
            case EQ -> executeLogicalOperator(Integer::equals);
            case NEQ -> executeLogicalOperator((a, b) -> !Objects.equals(a, b));
            case LE -> executeLogicalOperator((a, b) -> a < b);
            case LEQ -> executeLogicalOperator((a, b) -> a <= b);
            case GR -> executeLogicalOperator((a, b) -> a > b);
            case GEQ -> executeLogicalOperator((a, b) -> a >= b);
            case NOT -> {
                int b = S[SP] == 0 ? 1 : 0;
                S[SP] = b;
            }
            case NEG -> S[SP] = -S[SP];
            case LOAD -> {
                int a = S[SP];
                int k = instruction.getFirstArg();
                for (int i = 0; i < k; i++)
                    S[SP + i] = S[a + i];
                SP = SP + k - 1;
            }
            case STORE -> {
                int a = S[SP];
                int k = instruction.getFirstArg();
                for (int i = 0; i < k; i++)
                    S[a + i] = S[SP - k + i];
                SP--;
            }
            case LOADA -> {
                SP++;
                S[SP] = S[instruction.getFirstArg()];
            }
            case STOREA -> {
                S[instruction.getFirstArg()] = S[SP];
                SP--;
            }
            case POP -> SP = SP - instruction.getFirstArg();
            case JUMP -> PC = instruction.getFirstArg();
            case JUMPZ -> {
                int i = S[SP];
                SP--;

                if (i == 0) {
                    PC = instruction.getFirstArg();
                }
            }
            case JUMPI -> {
                PC = S[SP + instruction.getFirstArg()];
                SP--;
            }
            case DUP -> {
                SP++;
                S[SP] = S[SP - 1];
            }
            case ALLOC -> SP = SP + instruction.getFirstArg();
            //WEEK 2
            case HALT -> halted = true;
            case NEW -> {
                if (NP - S[SP] <= EP)
                    S[SP] = 0;
                else {
                    NP = NP - S[SP];
                    S[SP] = NP;
                }
            }
            case RJUMP -> PC += instruction.getFirstArg() - 1;
            case RJUMPZ -> {
                int i = S[SP];
                SP--;

                if (i == 0) {
                    PC += instruction.getFirstArg() - 1;
                }
            }
            case MARK -> {
                S[SP + 1] = EP;
                S[SP + 2] = FP;
                SP = SP + 2;
            }
            case CALL -> {
                int tmp = S[SP];
                S[SP] = PC;
                FP = SP;
                PC = tmp;
            }
            case ENTER -> {
                EP = SP + instruction.getFirstArg();
                if (EP >= NP) {
                    throw new RuntimeException("Stack Overflow");
                }
            }
            case RETURN -> {
                PC = S[FP];
                EP = S[FP - 2];
                if (EP >= NP) {
                    throw new RuntimeException("Stack Overflow");
                }
                SP = FP - 3;
                FP = S[SP + 2];
            }
            case SLIDE -> {
                int tmp = S[SP];
                SP = SP - instruction.getFirstArg();
                S[SP] = tmp;
            }
            case LOADRC -> {
                SP++;
                S[SP] = FP + instruction.getFirstArg();
            }
            case LOADR -> {
                SP++;
                S[SP] = S[FP + instruction.getFirstArg()];
            }
            case STORER -> {
                S[FP + instruction.getFirstArg()] = S[SP];
                SP--;
            }
            default -> throw new UnsupportedOperationException("Unknown instruction type: " + instruction.getType());
        }
    }
}
