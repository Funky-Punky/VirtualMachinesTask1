package tum.i2.cma;

import tum.i2.common.VirtualMachine;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class CMa implements VirtualMachine {

    int stacksize = 999;


    int sp = -1;
    int[] memory = new int[stacksize];

    int pc = 0;
    CMaInstruction[] instructions;

    public CMa(CMaInstruction[] instructions) {
        this.instructions = instructions;
    }

    @Override
    public void step() {
        CMaInstruction instruction = instructions[pc];
        printDebug();
        pc++;
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
        boolean x = true;

        while (true && x) {
            step();
        }

        return 0;
    }

    private void printDebug() {
        System.out.print("[");
        for (int i = 0; i < sp + 1; i++) {
            System.out.print(memory[i] + " ");
        }
        System.out.println("]");

//        if (pc < instructions.length) {
        System.out.println(instructions[pc]);
//        }
        System.out.println();
    }

    public void executeBinaryIntegerOperator(BiFunction<Integer, Integer, Integer> operator) {
        int i = operator.apply(memory[sp - 1], memory[sp]);
        memory[sp - 1] = i;
        sp--;
    }

    public void executeLogicalOperator(BiFunction<Integer, Integer, Boolean> operator) {
        int b = operator.apply(memory[sp - 1], memory[sp]) ? 1 : 0;
        memory[sp - 1] = b;
        sp--;
    }

    public void execute(CMaInstruction instruction) {
        // CMaInstructionType enum contains comments,
        // describing where the operations are defined
        switch (instruction.getType()) {
            case LOADC -> {
                sp++;
                memory[sp] = instruction.getFirstArg();
            }
            case ADD -> {
                executeBinaryIntegerOperator(Math::addExact);
            }
            case SUB -> {
                executeBinaryIntegerOperator(Math::subtractExact);
            }
            case MUL -> {
                executeBinaryIntegerOperator(Math::multiplyExact);
            }
            case DIV -> {
                executeBinaryIntegerOperator(Math::divideExact);
            }
            case MOD -> {
                executeBinaryIntegerOperator(Math::floorMod);
            }
            case AND -> {
                executeLogicalOperator((a, b) -> a > 0 && b > 0);
            }
            case OR -> {
                executeLogicalOperator((a, b) -> a > 0 || b > 0);
            }
            case XOR -> {
                executeLogicalOperator((a, b) -> a > 0 ^ b > 0);
            }
            case EQ -> {
                executeLogicalOperator(Integer::equals);
            }
            case NEQ -> {
                executeLogicalOperator((a, b) -> !Objects.equals(a, b));
            }
            case LE -> {
                executeLogicalOperator((a, b) -> a < b);
            }
            case LEQ -> {
                executeLogicalOperator((a, b) -> a <= b);
            }
            case GR -> {
                executeLogicalOperator((a, b) -> a > b);
            }
            case GEQ -> {
                executeLogicalOperator((a, b) -> a >= b);
            }
            case NOT -> {
                int b = memory[sp] == 0 ? 1 : 0;
                memory[sp] = b;
            }
            case NEG -> {
                memory[sp] = -memory[sp];
            }
            case LOAD -> {
                memory[sp] = memory[memory[sp]];
            }
            case STORE -> {
                memory[sp - 1] = memory[memory[sp]];
                sp--;
            }
            case LOADA -> {
                sp++;
                memory[sp] = memory[instruction.getFirstArg()];
            }
            case STOREA -> {
                memory[instruction.getFirstArg()] = memory[sp];
                sp--;
            }
            case POP -> {
                sp--;
            }
            case JUMP -> {
                pc = instruction.getFirstArg();
            }
            case JUMPZ -> {
                int i = memory[sp];
                sp--;

                if (i == 0) {
                    pc = instruction.getFirstArg();
                }
            }
            case JUMPI -> {
                pc = memory[sp + instruction.getFirstArg()];
                sp--;
            }
            case DUP -> {
                sp++;
                memory[sp] = memory[sp - 1];
            }
            case ALLOC -> {
                sp = sp + instruction.getFirstArg();
            }
            default -> throw new UnsupportedOperationException("Unknown instruction type: " + instruction.getType());
        }
    }
}
