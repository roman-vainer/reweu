package com.shpp.p2p.cs.mkruchok.assignment13;


import com.shpp.p2p.cs.iartomov.assignment13.Assignment13Part1;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Assignment13Part1Test {


    @Test
    void imageCheck1() throws IOException {
        String[] name = {"1P.png"};
        assertEquals(1, Assignment13Part1.main(name));
    }
    @Test
    void imageCheck2() throws IOException {
        String[] name = {"2.png"};
        assertEquals(2, Assignment13Part1.main(name));
    }
    @Test
    void imageCheck3() throws IOException {
        String[] name = {"2CT.jpg"};
        assertEquals(2, Assignment13Part1.main(name));
    }
    @Test
    void imageCheck4() throws IOException {
        String[] name = {"2TP.png"};
        assertEquals(2, Assignment13Part1.main(name));
    }
    @Test
    void imageCheck5() throws IOException {
        String[] name = {"2transp.png"};
        assertEquals(2, Assignment13Part1.main(name));
    }
    @Test
    void imageCheck6() throws IOException {
        String[] name = {"2C.jpg"};
        assertEquals(2, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck7() throws IOException {
        String[] name = {"5C.png"};
        assertEquals(5, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck8() throws IOException {
        String[] name = {"8T.jpg"};
        assertEquals(8, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck9() throws IOException {
        String[] name = {"20B.png"};
        assertEquals(20, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck10() throws IOException {
        String[] name = {"20P.png"};
        assertEquals(20, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck11() throws IOException {
        String[] name = {"21.jpg"};
        assertEquals(21, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck12() throws IOException {
        String[] name = {"25C.png"};
        assertEquals(25, Assignment13Part1.main(name));
    }

    @Test
    void imageCheck13() throws IOException {
        String[] name = {"test.jpg"};
        assertEquals(4, Assignment13Part1.main(name));
    }
}
