package app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiPredicate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import util.TestUtils;

public class JOptionPaneTester implements Runnable
{
    private static final String spaces  = "    ";
    private static final JOptionPaneTester  tester  =
        new JOptionPaneTester();
    private JFrame  mainFrame   = new JFrame( "JOptionPaneTester" );
    
    public static void main(String[] args)
    {
        try
        {
            SwingUtilities.invokeAndWait( tester );
        }
        catch ( InterruptedException | InvocationTargetException exc )
        {
            exc.printStackTrace();
            System.exit( 1 );
        }
        
        JButton button  = (JButton)TestUtils.getComponent( "start" );
        tester.buttonClick( button );
        TestUtils.pause( 500 );
        
        BiPredicate<Component,Object>   findDialogPred   =
            (c,o) -> c.isVisible() && c instanceof JOptionPane;
        Component   comp    = TestUtils.getComponent( findDialogPred, null );
        if ( !(comp instanceof JOptionPane) )
            throw new Error( "malfunction: getOptionPane returned non-optionPane" );
        JDialog dialog  = TestUtils.getJDialogForComponent( comp, true );
        if ( dialog == null )
            throw new Error( "malfunction: getOptionPane returned null" );

        tester.print( dialog );
        tester.mainFrame.dispose();
    }

    @Override
    public void run()
    {
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setSize( new Dimension( 300, 300 ) );
        JButton start   = new JButton( "Start" );
        start.addActionListener( e -> startOptionPane());
        start.setName( "start" );
        mainFrame.getContentPane().add( start );
        mainFrame.setVisible( true );
    }
    
    private void print( JDialog dialog )
    {
        StringBuilder   bldr    = new StringBuilder();
        bldr.append( "JDialog: ").append( dialog.getTitle() )
            .append( ", " ).append( getHexHash( dialog ) )
            .append( " owner=" ).append(  dialog.getOwner() );
        System.out.println( bldr );
        print( dialog.getContentPane(), spaces );
    }
    
    private void print( Container container, String indent )
    {
        if ( container instanceof JDialog )
        {
        }
        else 
        {
            StringBuilder   bldr    = new StringBuilder( indent );
            if ( container instanceof JOptionPane )
            {
                JOptionPane     pane    = (JOptionPane)container;
                bldr.append( "JOptionPane: " ).append( pane.getMessage() )
                .append( ", " ).append( getHexHash( pane ) )
                .append( " parent=" ).append(  pane.getParent() );
            }
            else
                bldr.append( container );
            System.out.println( bldr );
            
            for ( Component child : container.getComponents() )
            {
                if ( child instanceof Container )
                    print( (Container)child, indent + spaces );
                else
                    System.out.println( indent + child );
            }
        }
    }
    
    private void buttonClick( JButton button )
    {
        new Thread( () -> button.doClick() ).start();
    }
    
    private String getHexHash( Object obj )
    {
        String  hex = Integer.toHexString( obj.hashCode() );
        return hex;
    }
    
    private static void startOptionPane()
    {
        JOptionPane.showConfirmDialog( tester.mainFrame, "question?" );
        TestUtils.pause( 500 );
    }
    
//    private class WindowMonitor extends WindowAdapter
//    {
//        @Override
//        public void windowClosing( WindowEvent evt )
//        {
//            System.out.println( "window closing" );
//            final String    save        = SAVE_TEXT;
//            final String    discard     = DISCARD_TEXT;
//            final String    cancel      = CANCEL_TEXT;
//            
//            final String    message     = "Do you want to save your changes?";
//            final String    title       = "Save or Discard Changes";
//            final int       optionType  = JOptionPane.DEFAULT_OPTION;
//            final int       messageType = JOptionPane.QUESTION_MESSAGE;
//            final Icon      icon        = null;
//            final String[]  options     = { save, discard, cancel };
//            final String    initialVal  = save;
//            
//                int option  = JOptionPane.showOptionDialog(
//                    CommentEditor.this, 
//                    message, 
//                    title, 
//                    optionType, 
//                    messageType, 
//                    icon, 
//                    options, 
//                    initialVal
//                );
//        }
//    }
}
