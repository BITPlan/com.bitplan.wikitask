/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.wikitask open source project
 *
 * Copyright 2015-2022 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.ssh;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * see <a href='http://www.jcraft.com/jsch/examples/PortForwardingL.java'>sch
 * PortForwardingL example</a>
 * 
 * @author wf
 *
 */
public class SSH {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.ssh");

  private String user;
  private String host;

  private Session session;
  private int forwardPort;
  public static int SSH_PORT = 22;

  /**
   * construct me for the given parameters
   * 
   * @param user
   * @param host
   */
  public SSH(String user, String host) {
    this.user = user;
    this.host = host;
  }

  /**
   * prepare the tunnel by connecting to the SSH port
   * 
   * @throws JSchException
   */
  public void createSession() throws JSchException {
    JSch jsch = new JSch();
    String sshhome = String.format("%s/.ssh", System.getProperty("user.home"));
    String privateKey = String.format("%s/id_rsa", sshhome);
    String knownHosts = String.format("%s/known_hosts", sshhome);
    jsch.setKnownHosts(knownHosts);
    jsch.addIdentity(privateKey);
    System.out.println("identity added ");

    setSession(jsch.getSession(user, host, SSH_PORT));
    if (debug)
      LOGGER.log(Level.INFO, "session created.");

    // disabling StrictHostKeyChecking may help to make connection but makes it
    // insecure
    // see
    // http://stackoverflow.com/questions/30178936/jsch-sftp-security-with-session-setconfigstricthostkeychecking-no
    //
    // java.util.Properties config = new java.util.Properties();
    // config.put("StrictHostKeyChecking", "no");
    // session.setConfig(config);
  }

  /**
   * connect
   * 
   * @throws JSchException
   */
  public void connect() throws JSchException {
    getSession().connect();
    if (debug)
      LOGGER.log(Level.INFO, "session connected.....");
  }

   /**
   * 
   * @param remotePort
   * @param rhost
   * @param localPort
   * @return assigned port for forwarding
   * @throws JSchException
   */
  public int forward(int remotePort,String rhost, int localPort) throws JSchException {
    // https://stackoverflow.com/questions/12699854/how-to-connect-to-rds-from-local-java-program-via-ec2-ssh-tunnel
    forwardPort = getSession().setPortForwardingL(localPort, rhost, remotePort);
    String msg = String.format("host: %d  -> %s : %d via assigned port %d",
        localPort, rhost, remotePort, forwardPort);
    if (debug)
      LOGGER.log(Level.INFO, msg);
    return forwardPort;
  }

  /**
   * execute the given command remotely and return the result
   * 
   * @param cmd
   * @return the string with the command result
   * @throws Exception
   */
  public String execute(String cmd) throws Exception {
    ChannelExec exec = (ChannelExec) getSession().openChannel("exec");
    exec.setCommand(cmd);
    exec.connect();
    InputStream outputstream_from_the_channel = exec.getInputStream();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(outputstream_from_the_channel));
    String cmdOutput="";
    String line;
    while ((line = reader.readLine()) != null) {
      cmdOutput+=line+"\n";
    }
    reader.close();
    return cmdOutput;

  }

  /**
   * disconnect the session
   */
  public void disconnect() {
    if (getSession() != null) {
      getSession().disconnect();
      setSession(null);
    }
  }

  /**
   * @return the session
   */
  public Session getSession() {
    return session;
  }

  /**
   * @param session
   *          the session to set
   */
  public void setSession(Session session) {
    this.session = session;
  }
}
