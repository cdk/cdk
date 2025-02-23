package swingjs;

import org.openscience.cdk.tools.ILoggingTool;

public class SwingJSLogger implements ILoggingTool {

    private Class<?> sourceClass;

	public SwingJSLogger(Class<?> sourceClass) {
    	this.sourceClass = sourceClass;
	}

	public static ILoggingTool create(Class<?> sourceClass) {
        return new SwingJSLogger(sourceClass);
    }

	@Override
	public void dumpSystemProperties() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStackLength(int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dumpClasspath() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Object object, Object... objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Object object, Object... objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatal(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Object object, Object... objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Object object, Object... objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLevel(int level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}