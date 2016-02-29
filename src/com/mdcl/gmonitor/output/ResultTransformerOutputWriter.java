/**
 * 
 */
package com.mdcl.gmonitor.output;

import java.util.List;
import java.util.Map;

/**
 * @author irin
 *
 */
public interface ResultTransformerOutputWriter {
	void doWrite(List<Map<String,String>> results) throws Exception;
}
