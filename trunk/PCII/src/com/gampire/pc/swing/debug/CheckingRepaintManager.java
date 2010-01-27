package com.gampire.pc.swing.debug;


import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * Borrowed from the Spin project : http://spin.sourceforge.net. Thanks guys.
 */
public class CheckingRepaintManager extends RepaintManager {

    private static CheckingRepaintManager instance;

    private static boolean installed;

    private CheckingRepaintManager() {
        // nothing to do
    }

    /**
     * Install the CheckingRepaintManager. This method can be called any number
     * of times.
     */
    public static void installCheckingRepaintManager() {

        if (!installed) {
            RepaintManager.setCurrentManager(getInstance());
            installed = true;
        }
    }

    /***************************************************************************
     * 
     * @return the Singleton CheckingRepaintManager.
     **************************************************************************/
    private static RepaintManager getInstance() {

        if (instance == null) {
            instance = new CheckingRepaintManager();
        }

        return instance;
    }

    /**
     * Overriden to check EDT rule.
     */
    @Override
	public synchronized void addInvalidComponent(JComponent component) {
        checkEDTRule(component);
        super.addInvalidComponent(component);
    }

    /**
     * Overriden to check EDT rule.
     */
    @Override
	public synchronized void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
        checkEDTRule(component);
        super.addDirtyRegion(component, x, y, w, h);
    }

    /**
     * Check EDT rule on access to the given component.
     * 
     * @param component
     *            component to be repainted
     */
    protected void checkEDTRule(Component component) {

        if (violatesEDTRule(component)) {
            EDTRuleViolation violation = new EDTRuleViolation(component);
            StackTraceElement[] stackTrace = violation.getStackTrace();
            try {
                for (int e = stackTrace.length - 1; e >= 0; e--) {
                    if (isLiableToEDTRule(stackTrace[e])) {
                        StackTraceElement[] subStackTrace = new StackTraceElement[stackTrace.length - e];
                        System.arraycopy(stackTrace, e, subStackTrace, 0, subStackTrace.length);
                        violation.setStackTrace(subStackTrace);
                    }
                }
            } catch (Exception ex) {
                // keep stackTrace
            }

            indicate(violation);
        }
    }

    /**
     * Does acces to the given component violate the EDT rule.
     * 
     * @param component
     *            accessed component
     * @return <code>true</code> if EDT rule is violated
     */
    protected boolean violatesEDTRule(Component component) {
        return !SwingUtilities.isEventDispatchThread() && component.isShowing();
    }

    /**
     * Is the given stackTraceElement liable to the EDT rule.
     * 
     * @param element
     *            element
     * @return <code>true</code> if the className of the given element denotes
     *         a subclass of <code>java.awt.Component</code>
     */
    protected boolean isLiableToEDTRule(StackTraceElement element) throws Exception {
        return Component.class.isAssignableFrom(Class.forName(element.getClassName()));
    }

    /**
     * Indicate a violation of the EDT rule. This default implementation throws
     * the given exception, subclasses may want to log the exception instead.
     * 
     * @param violation
     *            violation of EDT rule
     */
    protected void indicate(EDTRuleViolation violation) throws EDTRuleViolation {
        throw new IllegalArgumentException("((THREADING)) EDT threading rule violated");
    }

    private static class EDTRuleViolation extends RuntimeException {
        private Component component;

        public EDTRuleViolation(Component component) {
            this.component = component;
        }

        public Component getComponent() {
            return component;
        }
    }
}
