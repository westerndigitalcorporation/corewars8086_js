package il.co.codeguru.corewars_riscv.memory;

public class MemoryRegion {

    public MemoryRegion() {
        m_start = -1;
        m_end = -1;
    }

    public MemoryRegion(int start, int end) {
        m_start = start;
        m_end = end;
    }

    public boolean isInRegion(int asked) {
        return ((asked >= m_start) && (asked <= m_end));
    }

    public boolean equals(MemoryRegion a) {
        return m_start == a.m_start && m_end == a.m_end;
    }

    public int m_start;
    public int m_end; // inclusive at end
}