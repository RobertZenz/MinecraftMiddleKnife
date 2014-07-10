/*
 * Copyright 2012 Robert 'Bobby' Zenz. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Robert 'Bobby' Zenz ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Robert 'Bobby' Zenz OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Robert 'Bobby' Zenz.
 */
package org.bonsaimind.minecraftmiddleknife.pre16;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The main frame which will contain the Minecraft applet (actually this will
 * only contain our ContainerApplet which itself contains the Minecraft applet).
 */
public class ContainerFrame extends Frame {
	
	private static final long serialVersionUID = 5317438537200198099L;
	
	private Applet containerApplet;
	private boolean exitOnClose = true;
	
	public ContainerFrame(String title) throws HeadlessException {
		super(title);
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// Not needed...
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				Applet container = ContainerFrame.this.containerApplet;
				if (container != null) {
					container.stop();
					container.destroy();
					ContainerFrame.this.containerApplet = null;
				}
				
				if (exitOnClose) {
					System.exit(0);
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// Not needed...
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// Not needed...
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// Not needed...
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// Not needed...
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// Not needed...
			}
		});
		
		setLayout(new BorderLayout());
	}
	
	/**
	 * Get the ContainerApplet.
	 * 
	 * @return The ContainerApplet.
	 */
	public Applet getContainerApplet() {
		return containerApplet;
	}
	
	/**
	 * Returns if System.exit(0) is executed when closing the frame.
	 * 
	 * @return
	 */
	public boolean isExitOnClose() {
		return exitOnClose;
	}
	
	/**
	 * Set the ContainerApplet.
	 * 
	 * @param container The ContainerApplet.
	 */
	public void setContainerApplet(ContainerApplet container) {
		this.containerApplet = container;
		add("Center", container);
	}
	
	/**
	 * Set this to true if you want a System.exit(0) executed on window close.
	 * 
	 * @param exitOnClose
	 */
	public void setExitOnClose(boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}
}
