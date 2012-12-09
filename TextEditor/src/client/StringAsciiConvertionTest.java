package client;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringAsciiConvertionTest {

	@Test
	public void StringToAsciiTest(){
		String str = "0aA!@";
		assertEquals("48a97a65a33a64", StringAsciiConvertion.toAscii(str));
	}	

	@Test
	public void AsciiToStringTest(){
		String str = "72a81a120a89";
		assertEquals("HQxY", StringAsciiConvertion.asciiToString(str));
	}	
	
	@Test
	public void TwoThingTogetherTest(){
		String str = "Please Make \n" + "This Work";
		assertEquals(str, StringAsciiConvertion.asciiToString(StringAsciiConvertion.toAscii(str)));
	}
}