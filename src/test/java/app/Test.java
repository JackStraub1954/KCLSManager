package app;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Test
{
    public static void main(String[] args) throws AWTException
    {
        sendKeys( new Robot(), "The night is fine; The Walrus said..." );
    }
    //
    private static void sendKeys(Robot robot, String keys) {
        for (char c : keys.toCharArray()) {
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                throw new RuntimeException(
                    "Key code not found for character '" + c + "'");
            }
            robot.keyPress(keyCode);
            robot.delay(100);
            robot.keyRelease(keyCode);
            robot.delay(100);
        }
    }
}