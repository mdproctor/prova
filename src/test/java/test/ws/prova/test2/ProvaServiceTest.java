package test.ws.prova.test2;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import ws.prova.service.ProvaService;
import ws.prova.service.impl.ProvaServiceImpl;

/**
 * Tests for OSGi-enabled service ProvaService.
 * The rulebases use the new "osgi" protocol to pass messages between multiple in-process Prova engines.
 * 
 * @author alex.kozlenkov
 *
 */
public class ProvaServiceTest {

	@Test
	public void initialization() {
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);
	}

	@Test
	public void message_passing() {
		final String sender_rulebase = "rules/service/message_passing/sender.prova";
		final String receiver_rulebase = "rules/service/message_passing/receiver.prova";
		
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);

		String sender = service.instance("sender", "");
		org.junit.Assert.assertNotNull(sender);

		String receiver = service.instance("receiver", "");
		org.junit.Assert.assertNotNull(receiver);

		service.consult(receiver, receiver_rulebase, "receiver1");
		AtomicInteger count = new AtomicInteger();
		service.setGlobalConstant(receiver, "$Count", count);
		service.consult(sender, sender_rulebase, "sender1");

		try {
			synchronized(this) {
				wait(1000);
				org.junit.Assert.assertEquals(1,count.get());
			}
		} catch (Exception e) {
		}
	}

	@Test
	public void stream_advertisement() {
		final String producer_rulebase = "rules/service/stream_advertisement/producer.prova";
		final String subscriber_rulebase = "rules/service/stream_advertisement/subscriber.prova";
		final String broker_rulebase = "rules/service/stream_advertisement/broker.prova";
		
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);

		String broker = service.instance("broker", "");
		org.junit.Assert.assertNotNull(broker);

		String producer = service.instance("producer", "");
		org.junit.Assert.assertNotNull(producer);

		String subscriber = service.instance("subscriber", "");
		org.junit.Assert.assertNotNull(producer);

		service.consult(broker, broker_rulebase, "broker1");
		AtomicInteger count = new AtomicInteger();
		service.setGlobalConstant(broker, "$Count", count);
		
		service.consult(producer, producer_rulebase, "producer1");
		service.setGlobalConstant(producer, "$Count", count);

		service.consult(subscriber, subscriber_rulebase, "subscriber1");
		service.setGlobalConstant(subscriber, "$Count", count);

		try {
			synchronized(this) {
				wait(1000);
				org.junit.Assert.assertEquals(5,count.get());
			}
		} catch (Exception e) {
		}
	}

	@Test
	public void stream_unavailable_subscription() {
		final String producer_rulebase = "rules/service/stream_advertisement/producer.prova";
		final String subscriber_rulebase = "rules/service/stream_advertisement/subscriber2.prova";
		final String broker_rulebase = "rules/service/stream_advertisement/broker.prova";
		
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);

		String broker = service.instance("broker", "");
		org.junit.Assert.assertNotNull(broker);

		String producer = service.instance("producer", "");
		org.junit.Assert.assertNotNull(producer);

		String subscriber = service.instance("subscriber", "");
		org.junit.Assert.assertNotNull(producer);

		service.consult(broker, broker_rulebase, "broker1");
		AtomicInteger count = new AtomicInteger();
		service.setGlobalConstant(broker, "$Count", count);
		
		service.consult(producer, producer_rulebase, "producer1");
		service.setGlobalConstant(producer, "$Count", count);

		service.consult(subscriber, subscriber_rulebase, "subscriber1");
		service.setGlobalConstant(subscriber, "$Count", count);

		try {
			synchronized(this) {
				wait(1000);
				org.junit.Assert.assertEquals(3,count.get());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Test that the subscriber renews lease in time
	 */
	@Test
	public void stream_lease() {
		final String producer_rulebase = "rules/service/stream_lease/producer.prova";
		final String subscriber_rulebase = "rules/service/stream_lease/subscriber.prova";
		final String broker_rulebase = "rules/service/stream_lease/broker.prova";
		
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);

		String broker = service.instance("broker", "");
		org.junit.Assert.assertNotNull(broker);

		String producer = service.instance("producer", "");
		org.junit.Assert.assertNotNull(producer);

		String subscriber = service.instance("subscriber", "");
		org.junit.Assert.assertNotNull(producer);

		service.consult(broker, broker_rulebase, "broker1");
		AtomicInteger count = new AtomicInteger();
		service.setGlobalConstant(broker, "$Count", count);
		
		service.consult(producer, producer_rulebase, "producer1");
		service.setGlobalConstant(producer, "$Count", count);

		service.consult(subscriber, subscriber_rulebase, "subscriber1");
		service.setGlobalConstant(subscriber, "$Count", count);

		try {
			synchronized(this) {
				wait(3000);
				org.junit.Assert.assertEquals(6,count.get());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Test that the subscriber can inject dependencies
	 */
	@Test
	public void stream_injected() {
		final String producer_rulebase = "rules/service/stream_injected/producer.prova";
		final String producer2_rulebase = "rules/service/stream_injected/producer2.prova";
		final String subscriber_rulebase = "rules/service/stream_injected/subscriber.prova";
		final String broker_rulebase = "rules/service/stream_injected/broker.prova";
		
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);

		String broker = service.instance("broker", "");
		org.junit.Assert.assertNotNull(broker);

		String producer = service.instance("producer", "");
		org.junit.Assert.assertNotNull(producer);

		String producer2 = service.instance("producer2", "");
		org.junit.Assert.assertNotNull(producer2);

		String subscriber = service.instance("subscriber", "");
		org.junit.Assert.assertNotNull(producer);

		service.consult(broker, broker_rulebase, "broker1");
		AtomicInteger count = new AtomicInteger();
		service.setGlobalConstant(broker, "$Count", count);
		
		service.consult(producer, producer_rulebase, "producer1");
		service.setGlobalConstant(producer, "$Count", count);

		service.consult(producer2, producer2_rulebase, "producer2");
		service.setGlobalConstant(producer2, "$Count", count);

		service.consult(subscriber, subscriber_rulebase, "subscriber1");
		service.setGlobalConstant(subscriber, "$Count", count);

		try {
			synchronized(this) {
				wait(3000);
				org.junit.Assert.assertEquals(12,count.get());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Test passing lambda functions to nested producers
	 */
	@Test
	public void stream_lambda() {
		final String producer_rulebase = "rules/service/stream_lambda/producer.prova";
		final String producer2_rulebase = "rules/service/stream_lambda/producer2.prova";
		final String subscriber_rulebase = "rules/service/stream_lambda/subscriber.prova";
		final String broker_rulebase = "rules/service/stream_lambda/broker.prova";
		final String functional_rulebase = "rules/reloaded/functional.prova";
		
		ProvaService service = new ProvaServiceImpl();
		service.init();
		org.junit.Assert.assertNotNull(service);

		String broker = service.instance("broker", "");
		org.junit.Assert.assertNotNull(broker);

		String producer = service.instance("producer", "");
		org.junit.Assert.assertNotNull(producer);

		String producer2 = service.instance("producer2", "");
		org.junit.Assert.assertNotNull(producer2);

		String subscriber = service.instance("subscriber", "");
		org.junit.Assert.assertNotNull(producer);

		service.consult(broker, broker_rulebase, "broker1");
		AtomicInteger count = new AtomicInteger();
		service.setGlobalConstant(broker, "$Count", count);
		
		service.consult(producer, producer_rulebase, "producer1");
		service.setGlobalConstant(producer, "$Count", count);

		service.consult(producer2, functional_rulebase, "functional");
		service.consult(producer2, producer2_rulebase, "producer2");
		service.setGlobalConstant(producer2, "$Count", count);

		service.consult(subscriber, subscriber_rulebase, "subscriber1");
		service.setGlobalConstant(subscriber, "$Count", count);

		try {
			synchronized(this) {
				wait(3000);
				org.junit.Assert.assertEquals(12,count.get());
			}
		} catch (Exception e) {
		}
	}

}