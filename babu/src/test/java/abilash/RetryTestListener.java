package abilash;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import testng.TestCase;
import java.util.HashSet;
import java.util.Set;

/**
 * Listens for failed tests that need to be rerun.
 */
public class RetryTestListener extends TestListenerAdapter  {
    private static final  Logger logger = Logger.getLogger(RetryTestListener.class);
    private static int count = 1; 
    private static final int maxCount = 7;

    @Override
    public void onTestFailure(ITestResult tr) {   
        tr.setAttribute("retry.count", count);
        tr.setAttribute("retry.maxCount", maxCount);
        boolean cond = false;
        if(count < maxCount) {
            count++;
            try  {
                if(TestCase.driver == null)  {
                    tr.setStatus(ITestResult.SKIP);
                    return;
                }
               
                 cond = true;
                if(cond)  {
                    tr.setAttribute("retry", true);
                }
            } catch(Exception e)  {
                logger.error("COULD NOT RETRY TESTCASE: "+e.getMessage());
            } 

        } else  {
            logger.error("Number of retries expired.");
            tr.setStatus(ITestResult.FAILURE);
            // reset count
            count = 1; 
        }
        super.onTestFailure(tr);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        count = 1; 
    }
    
    
    @Override
    public void onFinish(ITestContext context) {
        for (int i = 0; i < context.getAllTestMethods().length; i++) {
            if (context.getAllTestMethods()[i].getCurrentInvocationCount() == 2) {
                if (context.getFailedTests().getResults(context.getAllTestMethods()[i]).size() == 2 
                        || context.getPassedTests().getResults(context.getAllTestMethods()[i]).size() == 1) {
                    context.getFailedTests().removeResult(context.getAllTestMethods()[i]);
                }
            }
        }
        
    }
    
    private Set<ITestNGMethod> findDuplicates(Set<ITestResult> listContainingDuplicates) {
        Set<ITestNGMethod> toRemove = new HashSet<ITestNGMethod>();
        Set<ITestNGMethod> testSet = new HashSet<ITestNGMethod>();
        
        for(ITestResult test : listContainingDuplicates)  {
            if (!testSet.add(test.getMethod())) {
                toRemove.add(test.getMethod());
            }    
        }
        return toRemove;
        
    }
}