package ru.hh.spellcorrector.morpher;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.CorrektorService;
import ru.hh.spellcorrector.dict.StreamDictionary;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.testng.Assert.assertEquals;
import static ru.hh.spellcorrector.morpher.Morphers.cutDoubleSteps;

public class CorrectorTest {

  private static final String TEST_DICT =
      "дизъюнкция|2.3\n" +
      "рыбная|14.24\n" +
      "ловля|7.2\n" +
      "двигатель|68.4\n" +
      "внутреннего|172.16\n" +
      "сгорания|4.48\n" +
      "test|10000.0\n";

  @BeforeClass
  public static void initDictionary() throws IOException {
    StreamDictionary.load(new ByteArrayInputStream(TEST_DICT.getBytes("utf-8")));
  }

  private CorrektorService corrector;

  @BeforeMethod
  public void intiCorrector() {
    corrector = CorrektorService.of(cutDoubleSteps(), StreamDictionary.getInstance(), true);
  }

  @Test
  public void testIdentity() {
    assertEquals(corrector.correctWord("дизъюнкция".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testDelete() {
    assertEquals(corrector.correctWord("Ъдизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъЪюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъюнкцияЪ".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъЯюнкцияУ".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testTranspose() {
    assertEquals(corrector.correctWord("ИДзъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъюнкцИЯ".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизЮЪнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("ИДзъюКНция".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testReplace() {
    assertEquals(corrector.correctWord("Иизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("Цизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизЦюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъюнкциЦ".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testInsert() {
    assertEquals(corrector.correctWord("Ддизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("Цдизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъЦюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correctWord("дизъюнкцияЦ".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testSplit() {
    assertEquals(corrector.correctWord("рыбнаяловля"), "рыбная ловля");
    assertEquals(corrector.correctWord("двигательвнутреннегосгорания"), "двигатель внутреннего сгорания");
  }

  @Test
  public void testCharset() {
    assertEquals(corrector.correctWord("lbp].yrwbz"), "дизъюнкция");
    assertEquals(corrector.correctWord("hs,yfzkjdkz"), "рыбная ловля");
    assertEquals(corrector.correctWord("рыбнаяkjdkz"), "рыбная ловля");
  }

}

