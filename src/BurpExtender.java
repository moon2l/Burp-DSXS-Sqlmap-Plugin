package burp;
import com.arirubinstein.burp.*;

public class BurpExtender {
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        callbacks.registerMenuItem("Copy DSXS parameters", new DSXSMenuItem());
        callbacks.registerMenuItem("Copy SqlMap parameters", new DSXSMenuItem());
		callbacks.issueAlert("Successfully Initialized DSXS/SQLMap Plugin");
    }
}

