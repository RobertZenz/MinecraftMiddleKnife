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

/**
 * Beans that support customized output of JSON text shall implement this interface.  
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface JSONAware {
	/**
	 * @return JSON text
	 */
	String toJSONString();
}
