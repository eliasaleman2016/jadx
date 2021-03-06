package jadx.utils;

import android.util.Log;
import jadx.dex.attributes.IAttributeNode;
import jadx.dex.attributes.JadxErrorAttr;
import jadx.dex.nodes.ClassNode;
import jadx.dex.nodes.MethodNode;

import java.util.HashSet;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ErrorsCounter {
	private static final String tag = "ErrorsCounter";

	private static final Set<Object> errorNodes = new HashSet<Object>();
	private static int errorsCount = 0;

	public static int getErrorCount() {
		return errorsCount;
	}

	public static void reset() {
		errorNodes.clear();
		errorsCount = 0;
	}

	private static void addError(IAttributeNode node, String msg, Throwable e) {
		errorNodes.add(node);
		errorsCount++;

		if (e != null) {
			if (e.getClass() == StackOverflowError.class) {
				// don't print full stack trace
				e = new StackOverflowError(e.getMessage());
				Log.e(tag, msg);
			} else {
				Log.e(tag, msg, e);
			}
			node.getAttributes().add(new JadxErrorAttr(e));
		} else {
			Log.e(tag, msg);
		}
	}

	public static String classError(ClassNode cls, String errorMsg, Throwable e) {
		String msg = formatErrorMsg(cls, errorMsg);
		addError(cls, msg, e);
		return msg;
	}

	public static String methodError(MethodNode mth, String errorMsg, Throwable e) {
		String msg = formatErrorMsg(mth, errorMsg);
		addError(mth, msg, e);
		return msg;
	}

	public static String methodError(MethodNode mth, String errorMsg) {
		return methodError(mth, errorMsg, null);
	}

	public static void printReport() {
		if (getErrorCount() > 0) {
			Log.e(tag, getErrorCount() + " errors occured in following nodes:");
			for (Object node : errorNodes) {
				Log.e(tag, "  " + node.getClass().getSimpleName() + ": " + node);
			}
			// LOG.error("You can run jadx with '-f' option to view low level instructions");
		}
	}

	public static String formatErrorMsg(ClassNode cls, String msg) {
		return msg + " in class: " + cls;
	}

	public static String formatErrorMsg(MethodNode mth, String msg) {
		return msg + " in method: " + mth;
	}

	private static String formatException(Throwable e) {
		if (e == null || e.getMessage() == null)
			return "";
		else
			return "\n error: " + e.getMessage();
	}

	public static String formatErrorMsg(ClassNode cls, String msg, Throwable e) {
		return formatErrorMsg(cls, msg) + formatException(e);
	}

	public static String formatErrorMsg(MethodNode mth, String msg, Throwable e) {
		return formatErrorMsg(mth, msg) + formatException(e);
	}
}
