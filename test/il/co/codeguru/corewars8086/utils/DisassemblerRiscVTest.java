package il.co.codeguru.corewars8086.utils;

import il.co.codeguru.corewars8086.cpu.riscv.RV32I;
import il.co.codeguru.corewars8086.cpu.riscv.instruction_formats.InstructionFormatBase;
import il.co.codeguru.corewars8086.utils.disassembler.DisassemblerRiscV;
import il.co.codeguru.corewars8086.utils.disassembler.IDisassembler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DisassemblerRiscVTest {

    IDisassembler disassembler;

    @Test
    public void pass()
    {
        assertTrue(true);
    }

    private byte[] loadInstructions(InstructionFormatBase[] instructions)
    {
        byte[] result = new byte[instructions.length * 4];
        int index = 0;
        for(InstructionFormatBase instruction : instructions)
        {
            int raw = instruction.getRaw();
            result[index]  = (byte)(raw & 0xFF);
            result[index+1]= (byte)((raw >> 8) & 0xFF);
            result[index+2]= (byte)((raw >> 16)& 0xFF);
            result[index+3]= (byte)((raw >> 24)& 0xFF);
            index += 4;
        }
        return result;
    }

    @Test
    public void testSimpleAdd() throws IDisassembler.DisassemblerException
    {
        byte[] testData = loadInstructions(new InstructionFormatBase[]{
                RV32I.instructionR(RV32I.Opcodes.Add, 3,1,2)
        });

        disassembler = new DisassemblerRiscV(testData,0,4);
        String opcode = disassembler.nextOpcode();

        assertEquals("ADD #3, #1, #2", opcode);
        assertEquals(4, disassembler.lastOpcodeSize());
    }
}
