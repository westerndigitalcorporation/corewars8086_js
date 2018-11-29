package il.co.codeguru.corewars8086.cpu.riscv;

import il.co.codeguru.corewars8086.cpu.exceptions.MisalignedMemoryLoadException;
import il.co.codeguru.corewars8086.cpu.riscv.instruction_formats.*;
import il.co.codeguru.corewars8086.memory.MemoryException;
import il.co.codeguru.corewars8086.memory.RealModeAddress;
import il.co.codeguru.corewars8086.memory.RealModeMemory;
import il.co.codeguru.corewars8086.utils.Logger;

import static il.co.codeguru.corewars8086.war.War.ARENA_SEGMENT;

public class InstructionRunner {

    private CpuStateRiscV state;
    private RealModeMemory memory;
    private CpuRiscV cpu;

    public InstructionRunner(CpuRiscV cpu) {
        this.cpu = cpu;
        this.state = cpu.getState();
        this.memory = cpu.getMemory();
    }

    /**
        ADDI (ADD immediate - I Type) adds the sign-extended 12-bit immediate to register rs1.
        Arithmetic overflow is ignored and the result is simply the low XLEN bits of the result.
        ADDI rd, rs1, 0 is used to implement the MV rd, rs1 assembler pseudo-instruction
     */
    public void addi(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) + i.getImmediate());
    }

    /**
        ADD (ADD - R Type) adds the registers rs1 and rs2 and stores the result in rd.
        Arithmetic overflow is ignored and the result is simply the low XLEN bits of the result.
     */
    public void add(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) + state.getReg(i.getRs2()));
    }

    /**
         SUB (SUB - R Type) subs the register rs2 from rs1 and stores the result in rd.
         Arithmetic overflow is ignored and the result is simply the low XLEN bits of the result.
     */
    public void sub(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) - state.getReg(i.getRs2()));
    }

    /**
     * AUIPC (add upper immediate to pc) is used to build pc-relative addresses and uses the U-type format.
     * AUIPC forms a 32-bit offset from the 20-bit U-immediate, filling in the lowest 12 bits with zeros, adds this offset to the pc, then places the result in register rd
     */
    public void auipc(InstructionU i) {
        state.setReg(i.getRd(), state.getPc() + (i.getImmediate() << 12)); // TODO:Set this in the U type instruciton
    }

    /**
     * LUI (load upper immediate) is used to build 32-bit constants and uses the U-type format.
     * LUI places the U-immediate value in the top 20 bits of the destination register rd, filling in the lowest 12 bits with zeros
     */
    public void lui(InstructionU i) {
        int mask = (1 << 12) - 1;
        state.setReg(i.getRd(), (state.getReg(i.getRd()) & mask) | (i.getImmediate() << 12));
    }

    /**
     * The SW (Store word) instruction stores 32-bit value from register rs2 to memory
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void sw(InstructionS i) throws MemoryException {
        memory.write32Bit(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImm())), state.getReg(i.getRs2()));
    }

    /**
     * The SH (Store Halfword) instruction stores 16-bit value from the low bits of register rs2 to memory
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void sh(InstructionS i) throws MemoryException {
        memory.write16Bit(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImm())), (short) state.getReg(i.getRs2()));
    }

    /**
     * The SB (Store Byte) instruction stores 8-bit value from the low bits of register rs2 to memory
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void sb(InstructionS i) throws MemoryException {
        memory.writeByte(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImm())), (byte) state.getReg(i.getRs2()));
    }

    /**
     * ANDI is a logical operation that performs bitwise AND on register rs1 and the sign-extended 12-bit immediate and place the result in rd
     */
    public void andi(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) & i.getImmediate());
    }
    /**
     * AND is a logical operation that performs bitwise AND on registers rs1 and rs2 and place the result in rd
     */
    public void and(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) & state.getReg(i.getRs2()));
    }

    /**
     * ORI is a logical operation that performs bitwise OR on register rs1 and the sign-extended 12-bit immediate and place the result in rd
     */
    public void ori(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) | i.getImmediate());
    }

    /**
     * OR is a logical operation that performs bitwise OR on registers rs1 and rs2 and place the result in rd
     */
    public void or(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) | state.getReg(i.getRs2()));
    }

    /**
     * XORI is a logical operation that performs bitwise XOR on register rs1 and the sign-extended 12-bit immediate and place the result in rd
     * Note, "XORI rd, rs1, -1" performs a bitwise logical inversion of register rs1(assembler pseudo-instruction NOT rd, rs)
     */
    public void xori(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) ^ i.getImmediate());
    }

    /**
     * XOR is a logical operation that performs bitwise XOR on registers rs1 and rs2 and place the result in rd
     */
    public void xor(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) ^ state.getReg(i.getRs2()));
    }

    /**
     * SLLI performs logical left shift on the value in register rs1 by the shift amount held in the lower 5 bits of the immediate
     */
    public void slli(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) << i.getImmediate());
    }

    /**
     * SLL performs logical left shift on the value in register rs1 by the shift amount held in the lower 5 bits of register rs2
     */
    public void sll(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) << state.getReg(i.getRs2()));
    }

    /**
     * SRLI performs logical right shift on the value in register rs1 by the shift amount held in the lower 5 bits of the immediate
     */
    public void srli(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) >>> i.getImmediate());
    }

    /**
     * SRL performs logical right shift on the value in register rs1 by the shift amount held in the lower 5 bits of register rs2
     */
    public void srl(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) >>> state.getReg(i.getRs2()));
    }

    /**
     * SRAI performs arithmetic right shift on the value in register rs1 by the shift amount held in the lower 5 bits of the immediate
     */
    public void srai(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) >> i.getImmediate());
    }

    /**
     * SRA performs arithmetic right shift on the value in register rs1 by the shift amount held in the lower 5 bits of register rs2
     */
    public void sra(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) >> state.getReg(i.getRs2()));
    }

    /**
     * SLTI (set less than immediate) places the value 1 in register rd if register rs1 is less than the sign-extended immediate when both are treated as signed numbers, else 0 is written to rd.
     */
    public void slti(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) < i.getImmediate() ? 1 : 0);
    }

    /**
     * SLT (set less than) places the value 1 in register rd if register rs1 is less than register rs2 when both are treated as signed numbers, else 0 is written to rd.
     */
    public void slt(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) < state.getReg(i.getRs2()) ? 1 : 0);
    }
    /**
     * SLTIU (set less than immediate unsigned) places the value 1 in register rd if register rs1 is less than the immediate when both are treated as unsigned numbers, else 0 is written to rd.
     */
    public void sltiu(InstructionI i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) + 0x80000000 < i.getImmediate() + 0x80000000 ? 1 : 0);
    }
    /**
     * SLTU (set less than unsigned) places the value 1 in register rd if register rs1 is less than register rs2 when both are treated as unsigned numbers, else 0 is written to rd.
     */
    public void sltu(InstructionR i) {
        state.setReg(i.getRd(), state.getReg(i.getRs1()) + 0x80000000 < state.getReg(i.getRs2()) + 0x80000000 ? 1 : 0);
    }

    /**
     * The LW instruction loads a 32-bit value from memory into rd
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void lw(InstructionI i) throws MemoryException {

        state.setReg(i.getRd(), memory.read32Bit(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImmediate()))));
    }

    /**
     * LH loads a 16-bit value from memory,then sign-extends to 32-bits before storing in rd
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void lh(InstructionI i) throws MemoryException {
        state.setReg(i.getRd(), memory.read16Bit(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImmediate()))));
    }

    /**
     * LHU loads a 16-bit value from memory,then zero-extends to 32-bits before storing in rd
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void lhu(InstructionI i) throws MemoryException {
        int val = memory.read16Bit(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImmediate())));
        state.setReg(i.getRd(), val & 0xFFFF);
    }

    /**
     * LB loads a 8-bit value from memory,then sign-extends to 32-bits before storing in rd
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void lb(InstructionI i) throws MemoryException {
        state.setReg(i.getRd(), memory.readByte(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImmediate()))));
    }

    /**
     * LBU loads a 8-bit value from memory,then zero-extends to 32-bits before storing in rd
     * The effective byte address is obtained by adding register rs1 to the sign-extended 12-bit offset
     */
    public void lbu(InstructionI i) throws MemoryException {
        int val = memory.readByte(new RealModeAddress(ARENA_SEGMENT, (short) (state.getReg(i.getRs1()) + i.getImmediate())));
        state.setReg(i.getRd(), val & 0xFF);
    }

    /**
     * The  jump  and  link  (JAL)  instruction  uses  the  J-type  format,  where  the  J-immediate  encodes  a signed offset in multiples of 2 bytes.
     * The offset is sign-extended and added to the pc to form the jump target address.  Jumps can therefore target a +-1 MiB range.
     * JAL stores the address of the instruction following the jump (pc+4) into register rd.
     */
    public void jal(InstructionUJ i) throws MisalignedMemoryLoadException {
        state.setReg(i.getRd(), state.getPc() + 4);
        jump(state, i.getImmediate());
    }

    /**
     * The indirect jump instruction JALR (jump and link register) uses the I-type encoding.
     * The target address is obtained by adding the 12-bit signed I-immediate to the register rs1, then setting the least-significant bit of the result to zero.
     * The address of the instruction following the jump (pc+4)is written to register rd.
     * Register x0 can be used as the destination if the result is not required
     */
    public void jalr(InstructionI i) throws MisalignedMemoryLoadException {
        state.setReg(i.getRd(), state.getPc() + 4);
        jump(state, state.getReg(i.getRs1()) + i.getImmediate());
    }

    /**
     * BEQ (Branch if Equal) takes the branch if registers rs1 and rs2 are equal
     */
    public void beq(InstructionSB i) throws MisalignedMemoryLoadException {
        if (state.getReg(i.getRs1()) == state.getReg(i.getRs2())) jump(state, i.getImm());
    }

    /**
     * BNE (Branch if Not Equal) takes the branch if registers rs1 and rs2 are not equal
     */
    public void bne(InstructionSB i) throws MisalignedMemoryLoadException {
        if (state.getReg(i.getRs1()) != state.getReg(i.getRs2())) jump(state, i.getImm());
    }

    /**
     * BLT (Branch Less than) takes the branch if register rs1 is less than rs2 using signed comparision
     */
    public void blt(InstructionSB i) throws MisalignedMemoryLoadException {
        if (state.getReg(i.getRs1()) < state.getReg(i.getRs2())) jump(state, i.getImm());
    }

    /**
     * BLTU (Branch Less than Unsigned) takes the branch if register rs1 is less than rs2 using unsigned comparision
     */
    public void bltu(InstructionSB i) throws MisalignedMemoryLoadException {
        if (state.getReg(i.getRs1()) + 0x80000000 < state.getReg(i.getRs2()) + 0x80000000) jump(state, i.getImm());
    }

    /**
     * BGE (Branch Greater or Equal) takes the branch if register rs1 is greater than rs2 or equal using signed comparision
     */
    public void bge(InstructionSB i) throws MisalignedMemoryLoadException {
        if (state.getReg(i.getRs1()) >= state.getReg(i.getRs2())) jump(state, i.getImm());
    }

    /**
     * BGE (Branch Greater or Equal Unsigned) takes the branch if register rs1 is greater than rs2 or equal using unsigned comparision
     */
    public void bgeu(InstructionSB i) throws MisalignedMemoryLoadException {
        if (state.getReg(i.getRs1()) + 0x80000000 >= state.getReg(i.getRs2()) + 0x80000000) jump(state, i.getImm());
    }



    private void jump(CpuStateRiscV state, int immediate) throws MisalignedMemoryLoadException {
        if (immediate % 4 != 0) throw new MisalignedMemoryLoadException();
        state.setPc(state.getPc() + immediate - 4);
    }

}
