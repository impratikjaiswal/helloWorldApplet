package helloworldapplet;

import uicc.toolkit.ProactiveHandler;
import uicc.toolkit.ProactiveHandlerSystem;
import uicc.toolkit.ProactiveResponseHandler;
import uicc.toolkit.ProactiveResponseHandlerSystem;
import uicc.toolkit.ToolkitConstants;
import uicc.toolkit.ToolkitException;
import uicc.toolkit.ToolkitInterface;
import uicc.toolkit.ToolkitRegistry;
import uicc.toolkit.ToolkitRegistrySystem;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class helloWorldApplet extends Applet implements ToolkitInterface, ToolkitConstants
{
    protected static helloWorldApplet instance;

    private ToolkitRegistry toolkitRegistry;

    /** Display Text Command Qualifier **/
    private final static byte DT_CQ = (byte)0x81;

    /** DCS 8 bit DATA */
    private final static byte DCS_8_BIT_DATA = (byte)0x04;

    private static final byte VALUE_ZERO = (byte)0x00;

    private final static byte OFFSET_ZERO = (byte)0x00;

    private final static byte[] msg = { (byte)'T', (byte)'h', (byte)'i', (byte)'s', (byte)' ', (byte)'i', (byte)'s',
            (byte)' ', (byte)'f', (byte)'i', (byte)'r', (byte)'s', (byte)'t', (byte)' ', (byte)'H', (byte)'e',
            (byte)'l', (byte)'l', (byte)'o', (byte)' ', (byte)'f', (byte)'o', (byte)'r', (byte)'m', (byte)' ',
            (byte)'G', (byte)'l', (byte)'o', (byte)'b', (byte)'e', (byte)'T', (byte)'o', (byte)'u', (byte)'c',
            (byte)'h', (byte)'!', (byte)'!', (byte)'!' };

    public helloWorldApplet( )
    {
        // initialize object
        register();
        // register to the SIM Toolkit Framework
        this.toolkitRegistry = ToolkitRegistrySystem.getEntry();
        // register events
        this.toolkitRegistry.setEvent( EVENT_PROFILE_DOWNLOAD );
        this.toolkitRegistry.setEvent( EVENT_FIRST_COMMAND_AFTER_ATR );
    } // OptimusLaunchBrowser()

    public static void install( byte bArray[], short bOffset, byte blength ) throws ISOException
    {
        new helloWorldApplet();
    } // install()

    public void process( APDU apdu ) throws ISOException
    {
        if ( selectingApplet() )
        { // Select applet APDU command
            return;
        } // End of if ( selectingApplet() )
        byte buffer[] = apdu.getBuffer();
        // Proprietary Command
        // CLA INS P1 P2 P3
        // 00 44 00 00 32
        if ( buffer[ ISO7816.OFFSET_CLA ] != VALUE_ZERO )
        {
            ISOException.throwIt( ISO7816.SW_CLA_NOT_SUPPORTED );
        } // End of if ( buffer[ ISO7816.OFFSET_CLA ] != VALUE_ZERO )
        if ( buffer[ ISO7816.OFFSET_INS ] != (byte)0x44 )
        {
            ISOException.throwIt( ISO7816.SW_INS_NOT_SUPPORTED );
        } // End of if ( buffer[ ISO7816.OFFSET_INS ] != (byte)0x44 )
          // apdu.setIncomingAndReceive();
        processDT();
    } // End of public void process( APDU apdu ) throws ISOException

    /**
     * This function process the display text proactive command
     */
    private void processDT()
    {
        ProactiveHandler pro = ProactiveHandlerSystem.getTheHandler();
        ProactiveResponseHandler pror = ProactiveResponseHandlerSystem.getTheHandler();
        pro.init( PRO_CMD_PROVIDE_LOCAL_INFORMATION, (byte)0x00, DEV_ID_TERMINAL );
        pro.send();
        ProactiveHandler proHdlr = ProactiveHandlerSystem.getTheHandler();
        pro.init( PRO_CMD_DISPLAY_TEXT, (byte)DT_CQ, DEV_ID_TERMINAL );
        pro.appendTLV( TAG_TEXT_STRING, DCS_8_BIT_DATA, msg, OFFSET_ZERO, (short)msg.length );
        pro.send();
        /*
         * proHdlr.initDisplayText( DT_CQ, // qualifier (wait for user, prio high)
         * DCS_8_BIT_DATA, // dcs
         * msg,
         * OFFSET_ZERO,
         * (short)msg.length ); // length
         */
    }

    /**
     * To process the toolkit Events
     */
    public void processToolkit( short event ) throws ToolkitException
    {
        switch ( event )
        {
            case EVENT_PROFILE_DOWNLOAD:
                processDT();
                break;
        }
    }
}
