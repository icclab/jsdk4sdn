/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sdk4sdn;

import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;

/**
 *
 * @author aepp
 */
public class Sdk4Sdn {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		PluginManager pluginManager = new DefaultPluginManager();
		pluginManager.loadPlugins();
		pluginManager.startPlugins();
	}
	
}
