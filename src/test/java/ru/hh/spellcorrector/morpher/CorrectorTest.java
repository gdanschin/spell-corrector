package ru.hh.spellcorrector.morpher;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.hh.spellcorrector.SpellCorrector;
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
      "test|10000.0\n" ;

  @BeforeClass
  public static void initDictionary() throws IOException {
    StreamDictionary.load(new ByteArrayInputStream(TEST_DICT.getBytes("utf-8")));
  }

  private SpellCorrector corrector;

  @BeforeMethod
  public void intiCorrector() {
    corrector = SpellCorrector.of(cutDoubleSteps(), StreamDictionary.getInstance(), true);
  }

  @Test
  public void testIdentity() {
    assertEquals(corrector.correct("дизъюнкция".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testDelete() {
    assertEquals(corrector.correct("Ъдизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъЪюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъюнкцияЪ".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъЯюнкцияУ".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testTranspose() {
    assertEquals(corrector.correct("ИДзъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъюнкцИЯ".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизЮЪнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("ИДзъюКНция".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testReplace() {
    assertEquals(corrector.correct("Иизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("Цизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизЦюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъюнкциЦ".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testInsert() {
    assertEquals(corrector.correct("Ддизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("Цдизъюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъЦюнкция".toLowerCase()), "дизъюнкция");
    assertEquals(corrector.correct("дизъюнкцияЦ".toLowerCase()), "дизъюнкция");
  }

  @Test
  public void testSplit() {
    assertEquals(corrector.correct("рыбнаяловля"), "рыбная ловля");
    assertEquals(corrector.correct("двигательвнутреннегосгорания"), "двигатель внутреннего сгорания");
  }

  @Test
  public void testCharset() {
    assertEquals(corrector.correct("lbp].yrwbz"), "дизъюнкция");
    assertEquals(corrector.correct("hs,yfzkjdkz"), "рыбная ловля");
    assertEquals(corrector.correct("рыбнаяkjdkz"), "рыбная ловля");
  }

}

