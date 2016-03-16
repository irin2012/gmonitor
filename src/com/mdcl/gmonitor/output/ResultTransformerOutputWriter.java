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
	void doWrite(List<List<Map<String, String>>> results) throws Exception;
}
