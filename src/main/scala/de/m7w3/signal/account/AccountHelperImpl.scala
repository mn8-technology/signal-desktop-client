package de.m7w3.signal.account

import java.io.IOException

import de.m7w3.signal._
import de.m7w3.signal.messages.MessageSender
import org.whispersystems.signalservice.api.SignalServiceAccountManager
import org.whispersystems.signalservice.api.messages.multidevice.{RequestMessage, SignalServiceSyncMessage}
import org.whispersystems.signalservice.internal.push.SignalServiceProtos


private[account] case class AccountHelperImpl(userId: String, password: String, deviceId: Int, messageSender: MessageSender)
  extends PreKeyRefresher
    with AccountHelper
    with Logging {

  val accountManager: SignalServiceAccountManager = new SignalServiceAccountManager(
    Constants.SERVICE_URLS,
    userId,
    password,
    deviceId,
    Constants.USER_AGENT
  )

  override def countAvailablePreKeys(): Int = {
    accountManager.getPreKeysCount
  }

  @throws[IOException]
  override def requestSyncGroups(): Unit = {
    val r = SignalServiceProtos.SyncMessage.Request.newBuilder.setType(SignalServiceProtos.SyncMessage.Request.Type.GROUPS).build
    val message = SignalServiceSyncMessage.forRequest(new RequestMessage(r))
    logger.debug("requesting groups sync...")
    try {
      messageSender.send(message)
      logger.debug("groups sync requested.")
    } catch {
      case e: Throwable =>
        logger.error("Error requesting group synchronization", e)
        throw e

    }
  }

  @throws[IOException]
  override def requestSyncContacts(): Unit = {
    val r = SignalServiceProtos.SyncMessage.Request.newBuilder.setType(SignalServiceProtos.SyncMessage.Request.Type.CONTACTS).build
    val message = SignalServiceSyncMessage.forRequest(new RequestMessage(r))
    logger.debug("requesting contacts sync...")
    try {
      messageSender.send(message)
      logger.debug("contacts sync requested.")
    } catch {
      case e: Throwable =>
        logger.error("Error requesting contact synchronization", e)
        throw e
    }
  }
}
