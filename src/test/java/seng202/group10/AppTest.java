package seng202.group10;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    @org.junit.Test
     public void testApp()
    {
        assertTrue( true );
    }
    @org.junit.Test
    public void GeorgeTestApp()
    {
        assertTrue( true );
    }
    @org.junit.Test
    public void JonathanTestApp()
    {
        assertTrue( true );
    }
    @org.junit.Test
    public void BedeTestApp()
    {
        assertTrue( true );
    }
}
