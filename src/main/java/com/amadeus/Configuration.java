package com.amadeus;

import java.lang.NullPointerException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * The configuration for the Amadeus API client.
 */
@Accessors(chain = true)
@ToString
public class Configuration {
  private final HashMap<String, String> HOSTS = new HashMap<String, String>(){{
    put("production", "api.amadeus.com");
    put("test", "test.api.amadeus.com");
  }};

  /**
   * The client ID used to authenticate the API calls.
   * @param clientId The client ID
   * @return The client ID
   */
  private @Getter @Setter String clientId;
  /**
   * The client secret used to authenticate the API calls.
   * @param clientSecret The client secret
   * @return The client secret
   */
  private @Getter @Setter String clientSecret;
  /**
   * The logger that will be used to debug or warn to.
   * @param logger The logger object
   * @return The logger object
   */
  private @Getter @Setter Logger logger;
  /**
   * The log level. Can be 'silent', 'warn', or 'debug'.
   * Defaults to 'silent'.
   *
   * @param logLevel The log level for the logger
   * @return The log level for the logger
   */
  private @Getter @Setter String logLevel;
  /**
   * The the name of the server API calls are made to, 'production' or 'test'.
   * Defaults to 'test'
   *
   * @param hostname The name of the server API calls are made to
   * @return The name of the server API calls are made to
   */
  private @Getter String hostname;
  /**
   * The optional custom host domain to use for API calls.
   * Defaults to internal value for 'hostname'.
   *
   * @param host The optional custom host domain to use for API calls.
   * @return The optional custom host domain to use for API calls.
   */
  private @Getter @Setter String host;
  /**
   * Wether to use SSL. Defaults to True
   *
   * @param ssl A boolean specifying if the connection should use SSL
   * @return A boolean specifying if the connection should use SSL
   */
  private @Getter boolean ssl;
  /**
   * The port to use. Defaults to 443 for an SSL connection, and 80 for
   * a non SSL connection.
   *
   * @param port The port to use for the connection
   * @return The port to use for the connection
   */
  private @Getter @Setter int port;
  /**
   * An optional custom App ID to be passed in the User Agent to the
   * server (Defaults to null)
   *
   * @param port An optional custom App ID
   * @return The optional custom App ID
   */
  private @Getter @Setter String customAppId;
  /**
   * An optional custom App version to be passed in the User Agent to the
   * server (Defaults to null)
   *
   * @param port An optional custom App version
   * @return The optional custom App version
   */
  private @Getter @Setter String customAppVersion;


  // A protected override for the system environment
  protected @Setter Map<String, String> environment;

  protected Configuration() {
    this.environment = System.getenv();
    this.logger = Logger.getLogger("Amadeus");
    this.logLevel = "silent";
    this.hostname = "test";
    this.host = "test.api.amadeus.com";
    this.ssl = true;
    this.port = 443;
    // this.http =
  }

  /**
   * Builds an Amadeus client using the given documentation.
   *
   * @return an Amadeus client
   * @throws NullPointerException when a client ID or secret are missing
   */
  public Amadeus build() throws NullPointerException {
    parseEnvironment();
    ensureRequired("clientId", getClientId());
    ensureRequired("clientSecret", getClientSecret());
    return new Amadeus(this);
  }

  /**
   * The the name of the server API calls are made to, 'production' or 'test'.
   * Defaults to 'test'
   *
   * @param hostname The name of the server API calls are made to
   * @return The name of the server API calls are made to
   */
  public Configuration setHostname(String hostname) {
    if (!HOSTS.containsKey(hostname)) {
      throw new IllegalArgumentException(
              String.format("Hostname %s not found in %s", hostname,  HOSTS.keySet().toString()));
    }
    this.hostname = hostname;
    this.host = HOSTS.get(hostname);
    return this;
  }

  /**
   * Wether to use SSL. Defaults to True
   *
   * @param ssl A boolean specifying if the connection should use SSL
   * @return A boolean specifying if the connection should use SSL
   */
  public Configuration setSsl(Boolean ssl) {
    this.ssl = ssl;
    if (!ssl && port == 443) { setPort(80); }
    return this;
  }

  // Parses the environment
  private void parseEnvironment() {
    if (this.environment.containsKey("AMADEUS_CLIENT_ID")) {
      this.clientId = this.environment.get("AMADEUS_CLIENT_ID");
    }
    if (this.environment.containsKey("AMADEUS_CLIENT_SECRET")) {
      this.clientSecret = this.environment.get("AMADEUS_CLIENT_SECRET");
    }
    this.setEnvironment(null);
  }

  // Checks if a required value is present
  private void ensureRequired(String key, String value) throws NullPointerException {
    if (value == null) {
      String message = String.format("Missing required argument: %s", key);
      throw new NullPointerException(message);
    }
  }
}
