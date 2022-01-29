package kcls_manager.components;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import kcls_manager.main.Title;
import kcls_manager.main.Utils;

/**
 * Dialog to edit a title.
 * 
 * @author Jack Straub
 *
 */
public class TitleEditor extends JDialog
{
    /** For editing comments. Created once, reused as necessary. */
    private final CommentEditor     commentEditor           = 
        new CommentEditor( this );
    
    /** Default width of a text field */
    private static final int            TEXT_FIELD_WIDTH    = 15;
    
    /**
     * Restrict maximum spinner values to 5 digits;
     * this keeps the width of the dialog from blowing up.
     */ 
    private static final int            SPINNER_MAX        = 99_999;
    
    ////////////////////////////////////////////////////////////
    //
    // Set up models for numeric fields.
    //
    ////////////////////////////////////////////////////////////
    
    /** Model for rating field. */
    private final SpinnerNumberModel    ratingModel         =  
        new SpinnerNumberModel( -1, -1, 5, 1 );
    /** Model for rank field. */
    private final SpinnerNumberModel    rankModel           =  
        new SpinnerNumberModel( -10_000, -10_000, SPINNER_MAX, 1 );
    /** Model for place-queue-position field. */
    private final SpinnerNumberModel    placeQPosModel      =
        new SpinnerNumberModel( -1, -1, SPINNER_MAX, 1 );
    /** Model for reckon-queue-position field. */
    private final SpinnerNumberModel    reckonQPosModel     = 
        new SpinnerNumberModel( -1, -1, SPINNER_MAX, 1 );
    /** Model for check-queue-position field. */
    private final SpinnerNumberModel    checkQPosModel      = 
        new SpinnerNumberModel( -1, -1, SPINNER_MAX, 1 );

    private final LocalDateSpinner      createdSpinner      = 
        new LocalDateSpinner();
    private final LocalDateSpinner      modifiedSpinner     = 
        new LocalDateSpinner();
    private final LocalDateSpinner      reckonSpinner       = 
        new LocalDateSpinner();
    private final LocalDateSpinner      checkSpinner        = 
        new LocalDateSpinner();
    
    private final JSpinner      rankSpinner         = 
        new JSpinner( rankModel );
    private final JSpinner      ratingSpinner       = 
        new JSpinner( ratingModel );
    private final JSpinner      checkQPosSpinner    = 
        new JSpinner( checkQPosModel );
    private final JSpinner      reckonQPosSpinner   = 
        new JSpinner( reckonQPosModel );
    
    /** Sources drop-down */
    String[]    allSources  = 
    {
        "Unspecified",
        "Library",
        "Netflix",
        "Amazon",
        "Hulu",
        "HBO",
        "Paramount",
        "Peacock"
    };
    private final JComboBox<String> sourceComponent = new JComboBox<>( allSources );
    
    private final String    dateFormatPattern   = "MM/dd/yyyy";
    private int             selection   = -1;
    private Title           origTitle;
    private Title           newTitle;
    private JTextField      titleField  = new JTextField( TEXT_FIELD_WIDTH );
    private JTextField      authorField = new JTextField( TEXT_FIELD_WIDTH );
    
    public TitleEditor( Window owner )
    {
        super( 
            owner, 
            "Title Editor",
            Dialog.ModalityType.APPLICATION_MODAL
        );
        setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        setContentPane( new MainPanel() );
        addWindowListener( new WindowManager() );
        pack();
    }
    
    /**
     * Show/hide the dialog.
     * On "show", if title is non-null the dialog
     * will use it to populate it's fields.
     * 
     * @param visible   True to show the dialog, false to hide it
     * @param comment   Title to populate the dialog fields
     * 
     * @return
     *      A value indicating whether the dialog was dismissed
     *      by selecting the "save" or "cancel" button.
     */
    public int display( boolean visible, Title title )
    {
        if ( visible )
        {
            if ( title != null )
            {
                origTitle = title;
                newTitle = new Title( title );
            }
            else
            {
                origTitle = null;
                newTitle = new Title();
            }
            populateDialog();
        }
        setVisible( visible );
        return selection;
    }

    /**
     * Get a JPanel that stacks a label on top of a component.
     * 
     * @param label the label portion of the JPanel
     * @param comp  the component portion of the JPanel
     * @return
     */
    private JPanel getInputPairPanel( String label, JComponent comp )
    {
        JPanel  panel   = new JPanel( new GridLayout( 2, 1) );
        panel.add( new JLabel( label ) );
        panel.add( comp );
        return panel;
    }
    
    /**
     * Populate the dialog's fields with the contents
     * of the <em>data</em> instance variable.
     */
    private void populateDialog()
    {
        titleField.setText( newTitle.getTitle() );
        authorField.setText( newTitle.getAuthor() );
        ratingSpinner.setValue( newTitle.getRating() );
        rankSpinner.setValue( newTitle.getRank() );
        
        LocalDate       localDate;
        
        localDate = newTitle.getCreationDate();
        createdSpinner.setLocalDate( localDate );
        
        localDate = newTitle.getModifyDate();
        modifiedSpinner.setLocalDate( localDate );
        
        localDate = newTitle.getCheckDate();
        checkSpinner.setLocalDate( localDate );
        
        localDate = newTitle.getReckonDate();
        reckonSpinner.setLocalDate( localDate );
        
        checkQPosSpinner.setValue( newTitle.getCheckQPos() );
        reckonQPosSpinner.setValue( newTitle.getReckonQPos() );
        
        authorField.setText( newTitle.getAuthor() );
//        setSource();
    }
    
    private class MainPanel extends JPanel
    {
        /** Generated serial version UID */
        private static final long serialVersionUID = -1341518070288818212L;

        public MainPanel()
        {
            super( new BorderLayout() );
            int     bSize   = Utils.cvtEmToPxInt( 1, this );
            Border  border  = 
                BorderFactory.createEmptyBorder( bSize, bSize, bSize, bSize );
            setBorder( border );
            
            add( new CenterPanel(), BorderLayout.CENTER );
            add( new ButtonPanel(), BorderLayout.SOUTH );
            
            JSpinner.DateEditor dateEditor  = 
                new JSpinner.DateEditor( createdSpinner, dateFormatPattern );
            createdSpinner.setEditor( dateEditor );
            
            dateEditor = 
                new JSpinner.DateEditor( modifiedSpinner, dateFormatPattern );            
            modifiedSpinner.setEditor( dateEditor );
            
            dateEditor = 
                new JSpinner.DateEditor( checkSpinner, dateFormatPattern );            
            checkSpinner.setEditor( dateEditor );
            
            dateEditor = 
                new JSpinner.DateEditor( reckonSpinner, dateFormatPattern );            
            reckonSpinner.setEditor( dateEditor );
        }
    }

    private class CenterPanel extends JPanel
    {
        /** Generated serial version UID */
        private static final long serialVersionUID = -1591424186938706860L;

        public CenterPanel()
        {
            //super( new GridLayout( 1, 2, gap, gap ) );
            int         gap     = Utils.cvtEmToPxInt( 1f,  this );
            GridLayout  layout  = new GridLayout( 1, 2, gap, gap );
            setLayout( layout );
            
            int         bSize   = Utils.cvtEmToPxInt( 1, this );
            Border      empty   =
                BorderFactory.createEmptyBorder( bSize, bSize, bSize, bSize );
            Border      bevel   =
                BorderFactory.createBevelBorder( BevelBorder.LOWERED );
            Border      border  =
                BorderFactory.createCompoundBorder( bevel, empty );
            setBorder( border );
            add( new LeftCenterPanel() );
            add( new RightCenterPanel() );
        }
    }
 
    private class LeftCenterPanel extends JPanel
    {
        public LeftCenterPanel()
        {
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            add( getInputPairPanel( "Title", titleField ) );
            add( getInputPairPanel( "Author", authorField ) );
            
            JPanel  numberPanel = new JPanel( new GridLayout( 1, 2 ) );
            numberPanel.add( getInputPairPanel( "Rating", ratingSpinner ) );
            numberPanel.add( getInputPairPanel( "Rank", rankSpinner ) );
            add( numberPanel );
            
            add( getInputPairPanel( "Source", sourceComponent ) );
            JButton comments    = new JButton( "Edit Comments" );
            add( getInputPairPanel( " ", comments ) );
        }
    }
    
    private class RightCenterPanel extends JPanel
    {
        public RightCenterPanel()
        {
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            add( getInputPairPanel( "Date Created", createdSpinner ) );
            add( getInputPairPanel( "Date Modified", modifiedSpinner ) );
            add( getInputPairPanel( "Date Checked", checkSpinner ) );
            add( getInputPairPanel( "Date Reckoned From", reckonSpinner ) );
            
            JPanel  qPosPanel   = new JPanel( new GridLayout( 1, 2 ) );
            qPosPanel.add( getInputPairPanel( "Check QPos", checkQPosSpinner ) );
            qPosPanel.add( getInputPairPanel( "Reckon QPos", reckonQPosSpinner ) );
            add( qPosPanel );
        }
    }
    
    private class ButtonPanel extends JPanel
    {
        public ButtonPanel()
        {
            // This panel contains one additional panel; 
            // the additional panel contains the buttons, and is layed out
            // on the x-axis, forcing the buttons to occupy a row.
            // This panel has a y-axis layout, forcing the row of buttons
            // to be centered horizontally.
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            int     bSize   = Utils.cvtEmToPxInt( 1, this );
            Border  border  = 
                BorderFactory.createEmptyBorder( bSize, bSize, 0, bSize );
            setBorder( border );
            
            int         gap     = Utils.cvtEmToPxInt( .5f, this );
            Dimension   hGap    = new Dimension( gap, 0 );
                        
            JPanel  buttonRow   = new JPanel();
            buttonRow.setLayout( new BoxLayout( buttonRow, BoxLayout.X_AXIS ) );
            JButton okay    = new JButton( "OK" );
            JButton save    = new JButton( "Save" );
            JButton cancel  = new JButton( "Cancel" );
            buttonRow.add( okay );
            buttonRow.add( Box.createRigidArea( hGap ) );
            buttonRow.add( save );
            buttonRow.add( Box.createRigidArea( hGap ) );
            buttonRow.add( cancel );
            add( buttonRow );
        }
    }
    
//    private class ButtonPanel extends JPanel
//    {
//        public ButtonPanel()
//        {
//            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
//            int     bSize   = Utils.cvtEmToPxInt( 1, this );
//            Border  border  = 
//                BorderFactory.createEmptyBorder( bSize, bSize, bSize, bSize );
//            setBorder( border );
//            
//            int         gap     = Utils.cvtEmToPxInt( .5f, this );
//            Dimension   hGap    = new Dimension( gap, 0 );
//            Dimension   vGap    = new Dimension( 0, gap );
//            
//            JPanel  topRow  = new JPanel();
//            topRow.setLayout( new BoxLayout( topRow, BoxLayout.X_AXIS ) );
//            JButton edit    = new JButton( "Edit Selected" );
//            JButton delete  = new JButton( "Delete Selected" );
//            JButton insert  = new JButton( "Insert Row" );
//            topRow.add( edit );
//            topRow.add( Box.createRigidArea( hGap ) );
//            topRow.add( delete );
//            topRow.add( Box.createRigidArea( hGap ) );
//            topRow.add( insert );
//            add( topRow );
//            add( Box.createRigidArea( vGap ) );
//            
//            JPanel  bottomRow   = new JPanel();
//            bottomRow.setLayout( new BoxLayout( bottomRow, BoxLayout.X_AXIS ) );
//            JButton okay    = new JButton( "OK" );
//            JButton save    = new JButton( "Save" );
//            JButton cancel  = new JButton( "Cancel" );
//            bottomRow.add( okay );
//            bottomRow.add( Box.createRigidArea( hGap ) );
//            bottomRow.add( save );
//            bottomRow.add( Box.createRigidArea( hGap ) );
//            bottomRow.add( cancel );
//            add( bottomRow );
//        }
//    }
    
    private class WindowManager extends WindowAdapter
    {
        @Override
        public void windowClosing( WindowEvent evt )
        {
            display( false, null );
        }
    }
}
