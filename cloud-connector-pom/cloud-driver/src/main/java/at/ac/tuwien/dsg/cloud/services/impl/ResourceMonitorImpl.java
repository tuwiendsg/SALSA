package at.ac.tuwien.dsg.cloud.services.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.data.ResourceRequest;
import at.ac.tuwien.dsg.cloud.services.ResourceMonitor;

// THIS IS A SINGLETON SERVICE !
public class ResourceMonitorImpl implements ResourceMonitor {

	private Logger logger;
	private PrintWriter out;
	private BlockingQueue<ResourceRequest> queue;
	private ScheduledExecutorService fileWriter;

	private String toCVS(ResourceRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append(request.getId());
		sb.append(",");
		sb.append(request.getType());
		sb.append(",");
		sb.append(request.getStartTime());
		sb.append(",");
		sb.append(request.getEndTime());
		sb.append(",");
		sb.append(request.getImageID());
		sb.append(",");
		sb.append(request.getInstanceTypeName());
		sb.append(",");
		sb.append(request.getInstanceID());
		sb.append(",");

		for (String securityGroup : request.getSecurityGroups()) {
			sb.append(securityGroup);
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}

	public ResourceMonitorImpl(final Logger logger, File _outputFile,
			RegistryShutdownHub registryShutdownHub) throws IOException {
		this.logger = logger;
		this.out = new PrintWriter(new FileWriter(_outputFile, false));
		this.fileWriter = Executors.newSingleThreadScheduledExecutor();
		final String outputFile = _outputFile.getAbsolutePath();
		queue = new LinkedBlockingQueue<ResourceRequest>();

		final Runnable writeToFile = new Runnable() {
			public void run() {
				try {
					List<ResourceRequest> toWrite = new ArrayList<ResourceRequest>();
					int read = queue.drainTo(toWrite);
					logger.trace("read " + read
							+ " requests to write inside the file "
							+ outputFile);

					for (ResourceRequest request : toWrite) {
						logger.trace(toCVS(request));
						out.println(toCVS(request));
						out.flush();
					}

					toWrite.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		final ScheduledFuture<?> writerHandle = fileWriter.scheduleAtFixedRate(
				writeToFile, 5, 5, TimeUnit.SECONDS);

		registryShutdownHub.addRegistryShutdownListener(new Runnable() {
			@Override
			public void run() {
				try {
					writerHandle.cancel(true);
					fileWriter.shutdownNow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void record(ResourceRequest request) {
		try {
			// This is blocking !!
			queue.add(request);
		} catch (Throwable e) {
			logger.warn("record", e);
		}
	}
}
