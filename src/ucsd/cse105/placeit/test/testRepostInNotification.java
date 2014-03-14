package ucsd.cse105.placeit.test;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.robotium.solo.Solo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class testRepostInNotification extends BroadcastReceiver {

	private Solo solo;
	
	@Before
	public void setUp() throws Exception {
		solo = new Solo(null);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//super.onReceive();
	}
	
	

}
