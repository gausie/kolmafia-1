/**
 * Copyright (c) 2005, KoLmafia development team
 * http://kolmafia.sourceforge.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "KoLmafia development team" nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.kolmafia;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ListSelectionModel;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.lang.ref.WeakReference;

import net.java.dev.spellcast.utilities.ActionPanel;
import net.java.dev.spellcast.utilities.LicenseDisplay;
import net.java.dev.spellcast.utilities.LockableListModel;
import net.java.dev.spellcast.utilities.SortedListModel;
import net.java.dev.spellcast.utilities.JComponentUtilities;

public class KoLMenuBar extends JMenuBar implements KoLConstants
{
	protected static final FilenameFilter BACKUP_FILTER = new FilenameFilter()
	{
		public boolean accept( File dir, String name )
		{	return !name.startsWith( "." ) && !name.endsWith( "~" ) && !name.endsWith( ".bak" );
		}
	};

	protected ScriptMenu scriptMenu;
	protected BookmarkMenu bookmarkMenu;

	protected JMenuItem macroMenuItem = new ToggleMacroMenuItem();
	protected LockableListModel scripts = new LockableListModel();
	protected static SortedListModel bookmarks = new SortedListModel( String.class );

	private static final String [] LICENSE_FILENAME = {
		"kolmafia-license.gif", "spellcast-license.gif", "browserlauncher-license.htm",
		"sungraphics-license.txt", "systray-license.txt", "jline-license.txt" };

	private static final String [] LICENSE_NAME = {
		"KoLmafia", "Spellcast", "BrowserLauncher",
		"Sun Graphics", "System Tray", "JLine Terminal" };

	protected KoLMenuBar()
	{	this( null );
	}

	protected KoLMenuBar( JComponent container )
	{
		compileBookmarks();
		constructMenus( container == null ? this : container );
	}

	/**
	 * Utility method to compile the list of bookmarks based on the
	 * current server settings.
	 */

	protected static void compileBookmarks()
	{
		bookmarks.clear();
		String [] bookmarkData = StaticEntity.getProperty( "browserBookmarks" ).split( "\\|" );

		if ( bookmarkData.length > 1 )
			for ( int i = 0; i < bookmarkData.length; ++i )
				bookmarks.add( bookmarkData[i] + "|" + bookmarkData[++i] + "|" + bookmarkData[++i] );
	}

	/**
	 * Utility method which adds a menu bar to the frame.
	 * This is called by default to allow for all frames to
	 * have equivalent menu items.
	 */

	protected void constructMenus( JComponent container )
	{
		// Add general features.

		JMenu statusMenu = new JMenu( "General" );
		container.add( statusMenu );

		// Add the refresh menu, which holds the ability to refresh
		// everything in the session.

		statusMenu.add( new DisplayFrameMenuItem( "Adventure", "AdventureFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Purchases", "MallSearchFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Graphical CLI", "CommandDisplayFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Preferences", "OptionsFrame" ) );

		statusMenu.add( new JSeparator() );

		statusMenu.add( new DisplayFrameMenuItem( "Mini-Browser", "RequestFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Relay Browser", "LocalRelayServer" ) );
		statusMenu.add( new InvocationMenuItem( "KoL Simulator", StaticEntity.getClient(), "launchSimulator" ) );

		statusMenu.add( new JSeparator() );

		statusMenu.add( new DisplayFrameMenuItem( "Player Status", "CharsheetFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Item Manager", "ItemManageFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Gear Changer", "GearChangeFrame" ) );
		statusMenu.add( new DisplayFrameMenuItem( "Skill Casting", "SkillBuffFrame" ) );

		// Add specialized tools.

		JMenu toolsMenu = new JMenu( "Tools" );
		container.add( toolsMenu );

		toolsMenu.add( new InvocationMenuItem( "Clear Results", StaticEntity.getClient(), "resetSession" ) );
		toolsMenu.add( new StopEverythingItem() );
		toolsMenu.add( new InvocationMenuItem( "Refresh Session", StaticEntity.getClient(), "refreshSession" ) );

		toolsMenu.add( new JSeparator() );

		toolsMenu.add( new DisplayFrameMenuItem( "Store Manager", "StoreManageFrame" ) );
		toolsMenu.add( new DisplayFrameMenuItem( "Museum Display", "MuseumFrame" ) );
		toolsMenu.add( new DisplayFrameMenuItem( "Hagnk's Storage", "HagnkStorageFrame" ) );

		toolsMenu.add( new JSeparator() );

		toolsMenu.add( new DisplayFrameMenuItem( "Run a Buffbot", "BuffBotFrame" ) );
		toolsMenu.add( new DisplayFrameMenuItem( "Purchase Buffs", "BuffRequestFrame" ) );

		toolsMenu.add( new JSeparator() );

		toolsMenu.add( new DisplayFrameMenuItem( "Mushroom Plot", "MushroomFrame" ) );
		toolsMenu.add( new DisplayFrameMenuItem( "Flower Hunter", "FlowerHunterFrame" ) );
		toolsMenu.add( new DisplayFrameMenuItem( "Familiar Trainer", "FamiliarTrainingFrame" ) );
		toolsMenu.add( new DisplayFrameMenuItem( "Coin Toss Game", "MoneyMakingGameFrame" ) );

		// Add the old-school people menu.

		JMenu peopleMenu = new JMenu( "People" );
		container.add( peopleMenu );

		peopleMenu.add( new DisplayFrameMenuItem( "Read KoLmail", "MailboxFrame" ) );
		peopleMenu.add( new DisplayFrameMenuItem( "KoLmafia Chat", "KoLMessenger" ) );
		peopleMenu.add( new DisplayFrameMenuItem( "Clan Manager", "ClanManageFrame" ) );
		peopleMenu.add( new DisplayFrameMenuItem( "Recent Events", "EventsFrame" ) );

		peopleMenu.add( new JSeparator() );

		peopleMenu.add( new DisplayFrameMenuItem( "Write a KoLmail", "GreenMessageFrame" ) );
		peopleMenu.add( new DisplayFrameMenuItem( "Send a Package", "GiftMessageFrame" ) );
		peopleMenu.add( new DisplayFrameMenuItem( "Propose a Trade", "ProposeTradeFrame" ) );
		peopleMenu.add( new DisplayFrameMenuItem( "Pending Trades", "PendingTradesFrame" ) );

		// Add in common tasks menu

		JMenu travelMenu = new JMenu( "Travel" );
		container.add( travelMenu );

		travelMenu.add( new InvocationMenuItem( "Doc Galaktik", StaticEntity.getClient(), "makeGalaktikRequest" ) );
		travelMenu.add( new InvocationMenuItem( "Mind Control", StaticEntity.getClient(), "makeMindControlRequest" ) );

		travelMenu.add( new JSeparator() );

		travelMenu.add( new DisplayFrameMenuItem( "Hall of Legends", "MeatManageFrame" ) );
		travelMenu.add( new InvocationMenuItem( "Untinker Items", StaticEntity.getClient(), "makeUntinkerRequest" ) );
		travelMenu.add( new InvocationMenuItem( "Pulverize Items", StaticEntity.getClient(), "makePulverizeRequest" ) );

		travelMenu.add( new JSeparator() );

		travelMenu.add( new InvocationMenuItem( "Loot the Hermit", StaticEntity.getClient(), "makeHermitRequest" ) );
		travelMenu.add( new InvocationMenuItem( "Skin the Trapper", StaticEntity.getClient(), "makeTrapperRequest" ) );
		travelMenu.add( new InvocationMenuItem( "Mug the Hunter", StaticEntity.getClient(), "makeHunterRequest" ) );
		travelMenu.add( new InvocationMenuItem( "Trade for Gourds", StaticEntity.getClient(), "tradeGourdItems" ) );

		// Add in automatic quest completion scripts.

		JMenu questsMenu = new JMenu( "Quests" );
		container.add( questsMenu );

		questsMenu.add( new InvocationMenuItem( "Unlock Guild", StaticEntity.getClient(), "unlockGuildStore" ) );
		questsMenu.add( new InvocationMenuItem( "Tavern Quest", StaticEntity.getClient(), "locateTavernFaucet" ) );

		questsMenu.add( new JSeparator() );

		questsMenu.add( new InvocationMenuItem( "Nemesis Quest", Nemesis.class, "faceNemesis" ) );
		questsMenu.add( new InvocationMenuItem( "Leaflet (No Stats)", StrangeLeaflet.class, "leafletNoMagic" ) );
		questsMenu.add( new InvocationMenuItem( "Leaflet (With Stats)", StrangeLeaflet.class, "leafletWithMagic" ) );

		questsMenu.add( new JSeparator() );

		questsMenu.add( new InvocationMenuItem( "Lucky Entryway", SorceressLair.class, "completeCloveredEntryway" ) );
		questsMenu.add( new InvocationMenuItem( "Unlucky Entryway", SorceressLair.class, "completeCloverlessEntryway" ) );
		questsMenu.add( new InvocationMenuItem( "Hedge Rotation", SorceressLair.class, "completeHedgeMaze" ) );
		questsMenu.add( new InvocationMenuItem( "Tower Guardians", SorceressLair.class, "fightTowerGuardians" ) );
		questsMenu.add( new InvocationMenuItem( "Final Chamber", SorceressLair.class, "completeSorceressChamber" ) );

		// Add script and bookmark menus, which use the
		// listener-driven static lists.

		if ( !bookmarks.isEmpty() )
		{
			bookmarkMenu = new BookmarkMenu();
			container.add( bookmarkMenu );
		}

		if ( !scripts.isEmpty() )
		{
			scriptMenu = new ScriptMenu();
			container.add( scriptMenu );
		}

		if ( StaticEntity.getBooleanProperty( "showWindowMenu" ) )
			container.add( new WindowMenu() );

		// Add help information for KoLmafia.  This includes
		// the additional help-oriented stuffs.

		JMenu helperMenu = new JMenu( "Help" );
		container.add( helperMenu );

		helperMenu.add( new DisplayFrameMenuItem( "Copyright Notice", "LicenseDisplay" ) );
		helperMenu.add( new DisplayPageMenuItem( "Check for Updates", "https://sourceforge.net/project/showfiles.php?group_id=126572" ) );
		helperMenu.add( new DisplayPageMenuItem( "Donate to KoLmafia", "http://kolmafia.sourceforge.net/credits.html" ) );

		helperMenu.add( new JSeparator() );

		helperMenu.add( new DisplayFrameMenuItem( "Farmer's Alamanac", "CalendarFrame" ) );
		helperMenu.add( new DisplayFrameMenuItem( "Internal Database", "ExamineItemsFrame" ) );

		helperMenu.add( new JSeparator() );

		helperMenu.add( new DisplayPageMenuItem( "KoLmafia Thread", "http://forums.kingdomofloathing.com/viewtopic.php?t=19779" ) );
		helperMenu.add( new DisplayPageMenuItem( "End-User Manual", "http://kolmafia.sourceforge.net/manual.html" ) );
		helperMenu.add( new DisplayPageMenuItem( "Script Repository", "http://kolmafia.us/" ) );

		helperMenu.add( new JSeparator() );

		helperMenu.add( new DisplayPageMenuItem( "Subjunctive KoL", "http://www.subjunctive.net/kol/FrontPage.html" ) );
		helperMenu.add( new DisplayPageMenuItem( "KoL Visual Wiki", "http://kol.coldfront.net/thekolwiki/index.php/Main_Page" ) );
		helperMenu.add( new InvocationMenuItem( "Violet Fog Mapper", VioletFog.class, "showGemelliMap" ) );

		if ( !(container instanceof JMenuBar) )
			container.add( new InvocationMenuItem( "End Session", SystemTrayFrame.class, "removeTrayIcon" ) );
	}

	/**
	 * Internal class which displays the given request inside
	 * of the current frame.
	 */

	protected class DisplayRequestMenuItem extends JMenuItem implements ActionListener
	{
		private String location;

		public DisplayRequestMenuItem( String label, String location )
		{
			super( label );
			this.location = location;
			addActionListener( this );
		}

		public void actionPerformed( ActionEvent e )
		{
			if ( location.startsWith( "http" ) )
				StaticEntity.openSystemBrowser( location );
			else
				StaticEntity.openRequestFrame( location );
		}

		public String toString()
		{	return getText();
		}
	}

	protected class WindowMenu extends MenuItemList
	{
		public WindowMenu()
		{	super( "Window", existingFrames );
		}

		public JComponent constructMenuItem( Object o )
		{	return new WindowDisplayMenuItem( (KoLFrame) o );
		}

		public JComponent [] getHeaders()
		{
			JComponent [] headers = new JComponent[1];
			headers[0] = new WindowDisplayMenuItem( null );
			return headers;
		}

		private class WindowDisplayMenuItem extends JMenuItem implements ActionListener
		{
			private WeakReference frameReference;

			public WindowDisplayMenuItem( KoLFrame frame )
			{
				super( frame == null ? "Show All Displays" : frame.toString() );
				frameReference = frame == null ? null : new WeakReference( frame );
				addActionListener( this );
			}

			public void actionPerformed( ActionEvent e )
			{
				if ( frameReference == null )
				{
					KoLFrame [] frames = new KoLFrame[ existingFrames.size() ];
					existingFrames.toArray( frames );

					String interfaceSetting = StaticEntity.getProperty( "initialDesktop" );

					for ( int i = 0; i < frames.length; ++i )
						if ( interfaceSetting.indexOf( frames[i].getFrameName() ) == -1 )
							frames[i].setVisible( true );

					if ( KoLDesktop.instanceExists() )
						KoLDesktop.getInstance().setVisible( true );

					return;
				}

				KoLFrame frame = (KoLFrame) frameReference.get();
				if ( frame != null )
				{
					boolean appearsInTab = StaticEntity.getProperty( "initialDesktop" ).indexOf(
						frame instanceof ChatFrame ? "KoLMessenger" : frame.getFrameName() ) != -1;

					if ( !appearsInTab )
						frame.setVisible( true );

					frame.requestFocus();
				}
			}
		}
	}

	/**
	 * A special class which renders the menu holding the list of bookmarks.
	 * This class also synchronizes with the list of available bookmarks.
	 */

	protected class BookmarkMenu extends MenuItemList
	{
		public BookmarkMenu()
		{	super( "Bookmarks", bookmarks );
		}

		public JComponent constructMenuItem( Object o )
		{
			String [] bookmarkData = ((String)o).split( "\\|" );

			String name = bookmarkData[0];
			String location = bookmarkData[1];
			String pwdhash = bookmarkData[2];

			if ( pwdhash.equals( "true" ) )
				location += "&pwd";

			return new DisplayRequestMenuItem( name, location );
		}

		public JComponent [] getHeaders()
		{
			JComponent [] headers = new JComponent[2];

			headers[0] = new AddBookmarkMenuItem();
			headers[1] = new KoLPanelFrameMenuItem( "Manage Bookmarks", new BookmarkManagePanel() );

			return headers;
		}

		/**
		 * An internal class which handles the addition of new
		 * bookmarks to the bookmark menu.
		 */

		private class AddBookmarkMenuItem extends JMenuItem implements ActionListener
		{
			public AddBookmarkMenuItem()
			{
				super( "Add Bookmark..." );
				addActionListener( this );
			}

			public void actionPerformed( ActionEvent e )
			{
				AddBookmarkDialog prompter = new AddBookmarkDialog();
				prompter.pack();  prompter.setVisible( true );
			}
		}

		private class AddBookmarkDialog extends JDialog
		{
			public AddBookmarkDialog()
			{
				super( (java.awt.Frame) null, "Add a KoL-relative bookmark!" );
				getContentPane().add( new AddBookmarkPanel() );
			}

			private class AddBookmarkPanel extends KoLPanel
			{
				private JTextField nameField;
				private JTextField locationField;

				public AddBookmarkPanel()
				{
					super( "add", "cancel" );

					VerifiableElement [] elements = new VerifiableElement[2];
					nameField = new JTextField();
					elements[0] = new VerifiableElement( "Name", nameField );

					locationField = new JTextField( StaticEntity.getClient().getCurrentRequest() == null ? "" : StaticEntity.getClient().getCurrentRequest().getURLString() );
					elements[1] = new VerifiableElement( "Location", locationField );
					setContent( elements );
				}

				public void actionConfirmed()
				{
					// Strip pwdhash out of location and
					// set third parameter correctly

					bookmarks.add( nameField.getText() + "|" + locationField.getText() + "|false" );
					saveBookmarks();

					AddBookmarkDialog.this.dispose();
				}

				public void actionCancelled()
				{
					AddBookmarkDialog.this.dispose();
				}
			}
		}
	}

	/**
	 * A special panel which generates a list of bookmarks which
	 * can subsequently be managed.
	 */

	protected class BookmarkManagePanel extends ItemManagePanel
	{
		public BookmarkManagePanel()
		{
			super( "Bookmark Management", "rename", "delete", bookmarks );
			elementList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		}

		public void actionConfirmed()
		{
			int index = elementList.getSelectedIndex();
			if ( index == -1 )
				return;

			String currentItem = (String)elementList.getSelectedValue();
			if ( currentItem == null )
				return;

			String [] bookmarkData = currentItem.split( "\\|" );

			String name = bookmarkData[0];
			String location = bookmarkData[1];
			String pwdhash = bookmarkData[2];

			String newName = JOptionPane.showInputDialog( "Name your bookmark?", name );

			if ( newName == null )
				return;

			bookmarks.remove( index );
			bookmarks.add( newName + "|" + location + "|" + pwdhash );

			saveBookmarks();
		}

		public void actionCancelled()
		{
			int index = elementList.getSelectedIndex();
			if ( index == -1 )
				return;

			bookmarks.remove( index );
			saveBookmarks();
		}
	}

	public void compileScripts()
	{
		scripts.clear();

		// Get the list of files in the current directory
		File [] scriptList = SCRIPT_DIRECTORY.listFiles( BACKUP_FILTER );

		// Iterate through the files.  Do this in two
		// passes to make sure that directories start
		// up top, followed by non-directories.

		boolean hasDirectories = false;

		for ( int i = 0; i < scriptList.length; ++i )
		{
			if ( scriptList[i].isDirectory() )
			{
				scripts.add( scriptList[i] );
				hasDirectories = true;
			}
		}

		if ( hasDirectories )
			scripts.add( new JSeparator() );

		for ( int i = 0; i < scriptList.length; ++i )
			if ( !scriptList[i].isDirectory() )
				scripts.add( scriptList[i] );
	}

	/**
	 * Utility method to save the entire list of bookmarks to the settings
	 * file.  This should be called after every update.
	 */

	protected void saveBookmarks()
	{
		StringBuffer bookmarkData = new StringBuffer();

		for ( int i = 0; i < bookmarks.size(); ++i )
		{
			if ( i > 0 )
				bookmarkData.append( '|' );
			bookmarkData.append( (String) bookmarks.get(i) );
		}

		StaticEntity.setProperty( "browserBookmarks", bookmarkData.toString() );
	}

	private class ToggleMacroMenuItem extends JMenuItem implements ActionListener
	{
		public ToggleMacroMenuItem()
		{
			addActionListener( this );
			setText( KoLmafia.getMacroStream() instanceof NullStream ? "Record script" : "Stop record" );
		}

		public void actionPerformed( ActionEvent e )
		{
			if ( KoLmafia.getMacroStream() instanceof NullStream )
			{
				JFileChooser chooser = new JFileChooser( SCRIPT_DIRECTORY.getAbsolutePath() );
				int returnVal = chooser.showSaveDialog( null );

				if ( chooser.getSelectedFile() == null )
					return;

				String filename = chooser.getSelectedFile().getAbsolutePath();

				if ( returnVal == JFileChooser.APPROVE_OPTION )
					KoLmafia.openMacroStream( filename );

				macroMenuItem.setText( "Stop record" );
			}
			else
			{
				KoLmafia.closeMacroStream();
				macroMenuItem.setText( "Record script" );
			}
		}
	}

	protected abstract class ThreadedMenuItem extends JMenuItem implements ActionListener, Runnable
	{
		public ThreadedMenuItem( String title )
		{
			super( title );
			addActionListener( this );
		}

		public void actionPerformed( ActionEvent e )
		{	(new Thread( this )).start();
		}

		public final void run()
		{
			KoLmafia.forceContinue();
			executeTask();
			KoLmafia.enableDisplay();
		}

		protected abstract void executeTask();
	}

	/**
	 * In order to keep the user interface from freezing (or at least
	 * appearing to freeze), this internal class is used to process
	 * the request for loading a script.
	 */

	private class LoadScriptMenuItem extends ThreadedMenuItem
	{
		private String scriptPath;

		public LoadScriptMenuItem()
		{	this( "Load script...", null );
		}

		public LoadScriptMenuItem( String scriptName, String scriptPath )
		{
			super( scriptName );
			this.scriptPath = scriptPath;
		}

		public void executeTask()
		{
			String executePath = scriptPath;

			if ( scriptPath == null )
			{
				JFileChooser chooser = new JFileChooser( SCRIPT_DIRECTORY.getAbsolutePath() );
				int returnVal = chooser.showOpenDialog( null );

				if ( chooser.getSelectedFile() == null )
					return;

				if ( returnVal == JFileChooser.APPROVE_OPTION )
					executePath = chooser.getSelectedFile().getAbsolutePath();
			}

			if ( executePath == null )
				return;

			KoLmafia.forceContinue();
			DEFAULT_SHELL.executeLine( executePath );
		}
	}

	/**
	 * In order to keep the user interface from freezing (or at least
	 * appearing to freeze), this internal class is used to process
	 * the request for viewing frames.
	 */

	protected class DisplayFrameMenuItem extends ThreadedMenuItem
	{
		private String frameClass;

		public DisplayFrameMenuItem( String title, String frameClass )
		{
			super( title );
			this.frameClass = frameClass;
		}

		public void executeTask()
		{
			if ( frameClass.equals( "LicenseDisplay" ) )
			{
				Object [] parameters = new Object[4];
				parameters[0] = "KoLmafia: Copyright Notices";
				parameters[1] = new VersionDataPanel();
				parameters[2] = LICENSE_FILENAME;
				parameters[3] = LICENSE_NAME;

				(new CreateFrameRunnable( LicenseDisplay.class, parameters )).run();
			}
			else
			{
				KoLmafiaGUI.constructFrame( frameClass );
			}
		}
	}

	/**
	 * Internal class which opens the operating system's default
	 * browser to the given location.
	 */

	protected class DisplayPageMenuItem extends JMenuItem implements ActionListener
	{
		private String location;

		public DisplayPageMenuItem( String title, String location )
		{
			super( title );
			addActionListener( this );

			this.location = location;
		}

		public void actionPerformed( ActionEvent e )
		{	StaticEntity.openSystemBrowser( location );
		}
	}

	/**
	 * Internal class used to invoke the given no-parameter
	 * method on the given object.  This is used whenever
	 * there is the need to invoke a method and the creation
	 * of an additional class is unnecessary.
	 */

	protected class InvocationMenuItem extends ThreadedMenuItem
	{
		private Object object;
		private Method method;

		public InvocationMenuItem( String title, Object object, String methodName )
		{
			this( title, object == null ? null : object.getClass(), methodName );
			this.object = object;
		}

		public InvocationMenuItem( String title, Class c, String methodName )
		{
			super( title );

			if ( c == null )
				return;

			try
			{
				this.object = c;
				this.method = c.getMethod( methodName, NOPARAMS );
			}
			catch ( Exception e )
			{
				// This should not happen.  Therefore, print
				// a stack trace for debug purposes.

				StaticEntity.printStackTrace( e );
			}
		}

		public void executeTask()
		{
			try
			{
				if ( method != null )
					method.invoke( object instanceof KoLmafia ? StaticEntity.getClient() : object, null );
			}
			catch ( Exception e )
			{
				// This should not happen.  Therefore, print
				// a stack trace for debug purposes.

				StaticEntity.printStackTrace( e );
			}
		}
	}

	/**
	 * An internal class used to handle requests to open a new frame
	 * using a local panel inside of the adventure frame.
	 */

	protected class KoLPanelFrameMenuItem extends ThreadedMenuItem
	{
		private CreateFrameRunnable creator;

		public KoLPanelFrameMenuItem( String title, ActionPanel panel )
		{
			super( title );

			Object [] parameters = new Object[2];
			parameters[0] = title;
			parameters[1] = panel;

			creator = new CreateFrameRunnable( KoLPanelFrame.class, parameters );
		}

		public void executeTask()
		{	creator.run();
		}
	}

	/**
	 * An internal class which displays KoLmafia's current version
	 * information.  This is passed to the constructor for the
	 * <code>LicenseDisplay</code>.
	 */

	private class VersionDataPanel extends JPanel
	{
		private final String [] versionData = {
			VERSION_NAME, VERSION_DATE, " ",
			"Copyright � 2005 KoLmafia development team",
			"Berkeley Software Development (BSD) License",
			"http://kolmafia.sourceforge.net/",
			" ",
			"Current Running on " + System.getProperty( "os.name" ),
			"Using Java v" + System.getProperty( "java.runtime.version" )
		};

		public VersionDataPanel()
		{
			JPanel versionPanel = new JPanel( new BorderLayout( 20, 20 ) );
			versionPanel.add( new JLabel( JComponentUtilities.getImage( "penguin.gif" ), JLabel.CENTER ), BorderLayout.NORTH );

			JPanel labelPanel = new JPanel( new GridLayout( versionData.length, 1 ) );
			for ( int i = 0; i < versionData.length; ++i )
				labelPanel.add( new JLabel( versionData[i], JLabel.CENTER ) );

			versionPanel.add( labelPanel, BorderLayout.CENTER );

			JButton donateButton = new JButton( JComponentUtilities.getImage( "paypal.gif" ) );
			JComponentUtilities.setComponentSize( donateButton, 74, 31 );
			donateButton.addActionListener( new DisplayPageMenuItem( "", "http://sourceforge.net/project/project_donations.php?group_id=126572" ) );

			JPanel donatePanel = new JPanel();
			donatePanel.add( donateButton );

			JPanel centerPanel = new JPanel( new BorderLayout( 20, 20 ) );
			centerPanel.add( versionPanel, BorderLayout.CENTER );
			centerPanel.add( donatePanel, BorderLayout.SOUTH );

			setLayout( new CardLayout( 20, 20 ) );
			add( centerPanel, "" );
		}
	}

	/**
	 * A special class which renders the list of available scripts.
	 */

	private class ScriptMenu extends MenuItemList
	{
		public ScriptMenu()
		{
			super( "Scripts", scripts );

			if ( !SCRIPT_DIRECTORY.exists() )
				SCRIPT_DIRECTORY.mkdirs();

			compileScripts();
		}

		public JComponent constructMenuItem( Object o )
		{	return o instanceof JSeparator ? new JSeparator() : constructMenuItem( (File) o, "scripts" );
		}

		private JComponent constructMenuItem( File file, String prefix )
		{
			// Get path components of this file
			String [] pieces;

			try
			{
				pieces = file.getCanonicalPath().split( "[\\\\/]" );
			}
			catch ( Exception e )
			{
				// This should not happen.  Therefore, print
				// a stack trace for debug purposes.

				StaticEntity.printStackTrace( e );
				return null;
			}

			// There must be at least a file name
			if ( pieces.length < 1 )
				return null;

			String name = pieces[ pieces.length - 1 ];
			String path = prefix + File.separator + name;

			if ( file.isDirectory() )
			{
				// Get a list of all the files
				File [] scriptList = file.listFiles();

				//  Convert the list into a menu
				JMenu menu = new JMenu( name );

				// Iterate through the files.  Do this in two
				// passes to make sure that directories start
				// up top, followed by non-directories.

				boolean hasDirectories = false;

				for ( int i = 0; i < scriptList.length; ++i )
				{
					if ( scriptList[i].isDirectory() )
					{
						menu.add( constructMenuItem( scriptList[i], path ) );
						hasDirectories = true;
					}
				}

				if ( hasDirectories )
					menu.add( new JSeparator() );

				for ( int i = 0; i < scriptList.length; ++i )
					if ( !scriptList[i].isDirectory() )
						menu.add( constructMenuItem( scriptList[i], path ) );

				// Return the menu
				return menu;
			}

			return new LoadScriptMenuItem( name, path );
		}

		public JComponent [] getHeaders()
		{
			JComponent [] headers = new JComponent[3];

			headers[0] = new LoadScriptMenuItem();
			headers[1] = macroMenuItem;
			headers[2] = new InvocationMenuItem( "Refresh menu", KoLMenuBar.this, "compileScripts" );

			return headers;
		}
	}

	private class RequestMenuItem extends JMenuItem implements ActionListener
	{
		private KoLRequest request;

		public RequestMenuItem( String title, KoLRequest request )
		{
			super( title );
			this.request = request;
			addActionListener( this );
		}

		public void actionPerformed( ActionEvent e )
		{	(new RequestThread( request )).start();
		}
	}

	private class StopEverythingItem extends JMenuItem implements ActionListener
	{
		public StopEverythingItem()
		{
			super( "Stop Everything" );
			addActionListener( this );
		}

		public void actionPerformed( ActionEvent e )
		{	StaticEntity.getClient().declareWorldPeace();
		}
	}
}
