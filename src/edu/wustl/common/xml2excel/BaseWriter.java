/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/wustl-common-package/LICENSE.txt for details.
 */

package edu.wustl.common.xml2excel;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public abstract class BaseWriter {
	public abstract void generate(String xml, HttpServletResponse resp) throws IOException;	
	public abstract int getColsStat();
	public abstract int getRowsStat();
	public abstract void setWatermark(String watermark);
	public abstract void setFontSize(int fontsize);
}
