/*
 * Name: json-simple
 * Author: Yidong Fang 
 * URL: http://code.google.com/p/json-simple/
 * License: Apache
 * Revision: 219
 * 
 * THIS FILE IS NOT PART OF 'MinecraftMiddleKnife' AND IS ONLY COPIED HERE FOR
 * EASE OF USE! THIS SOURCE CODE IS UNDER A APACHE LICENSE, FOR DETAILS
 * PLEASE VISIT THE ABOVE LINK.
 */

package org.json.simple;

import java.io.IOException;
import java.io.Writer;

/**
 * Beans that support customized output of JSON text to a writer shall implement this interface.  
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JSONStreamAware {

	/**
	 * write JSON string to out.
	 */
	void writeJSONString(Writer out) throws IOException;
}
