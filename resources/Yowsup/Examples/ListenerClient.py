'''
Copyright (c) <2012> Tarek Galal <tare2.galal@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
'''

import os
parentdir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.sys.path.insert(0,parentdir)
import datetime, sys
import time

if sys.version_info >= (3, 0):
	raw_input = input

from Yowsup.connectionmanager import YowsupConnectionManager

class WhatsappListenerClient:

	def startConnection(self):
		connectionManager = YowsupConnectionManager()
		connectionManager.setAutoPong(True)

		self.signalsInterface = connectionManager.getSignalsInterface()
		self.methodsInterface = connectionManager.getMethodsInterface()

		self.signalsInterface.registerListener("message_received", self.onMessageReceived)
		self.signalsInterface.registerListener("auth_success", self.onAuthSuccess)
		self.signalsInterface.registerListener("auth_fail", self.onAuthFailed)
		self.signalsInterface.registerListener("disconnected", self.onDisconnected)
		self.signalsInterface.registerListener("receipt_messageSent", self.onMessageSent)
		self.gotReceipt = 0
		self.cm = connectionManager
		self.fileName="defaultFileName"

	def __init__(self, resourceLocation):
		self.resourceLocation = resourceLocation
		self.startConnection()

	def login(self, username, password):
		self.username = username
		self.password =  password
		self.authed = False
		self.methodsInterface.call("auth_login", (username, password))
		self.authorizing = True
		#commandFile="/home/deepak/HI/WS_BuildMessenger/BuildMessenger/src/main/resources/commandFile"
		commandFile="%s/tmp/commandFile"%self.resourceLocation
		self.commandFileObj=open(commandFile)
		done = False
		while not done:
			inputLine = self.commandFileObj.readline()
			if not inputLine:
      				time.sleep(1)
       				continue
			#logfileName="/home/deepak/HI/WS_BuildMessenger/BuildMessenger/src/main/resources/log"
			if inputLine == "Done":
				done = True
				continue
			inputLine=inputLine.split("\n",1)[0]
			splits=inputLine.split('|',1)
			self.fileName="%s/tmp/%s"%(self.resourceLocation,splits[0])
			target=splits[1]
			fobj=open(self.fileName,"r+")
			message=fobj.read()
			now_text = datetime.datetime.now().strftime("%c")
			logfileName="%s/logs/To.log"%self.resourceLocation
			logFileObj=open(logfileName,"a+")
			logFileObj.write("------------------------------------\n")
			logFileObj.write("To:%s:\t At: %s\nMessage:\n%s\n"%(target,now_text,message))
			logFileObj.write("------------------------------------\n")
			logFileObj.close()
			fobj.seek(0)
			fobj.truncate()
			fobj.close()
			#print(target)
			if '-' in target:
				jids = ["%s@g.us" % target]
			else:
				jids = ["%s@s.whatsapp.net" % t for t in target.split(',')]

			#print(jids)
			while not self.authed:
				if not self.authorizing:
					self.startConnection()
					self.methodsInterface.call("auth_login", (username, password))
					self.authorizing = True
				time.sleep(1)
			self.methodsInterface.call("ready")
			length = len(jids)
			i = 0
			while i < length:
				self.methodsInterface.call("message_send", (jids[i], message))
				i+=1
			#print("Sent message")
			#if self.waitForReceipt:
			timeout = 5
			t = 0;
			self.gotReceipt = 0
			while t < timeout and self.gotReceipt < length:
				time.sleep(1)
				t+=1
			fobj=open(self.fileName,"a+")
			recieptResult="Done"
			if self.gotReceipt < length:
				recieptResult="Timed Out"
			fobj.write(recieptResult)
			fobj.close()
		self.commandFileObj.close()

	def onAuthSuccess(self, username):
		print("Authed %s" % username)
		sys.stdout.flush()
		self.methodsInterface.call("ready")
		self.authed = True
		self.authorizing = False

	def onAuthFailed(self, username, err):
		print("Auth Failed!")
		sys.stdout.flush()
		sys.exit(1)

	def onDisconnected(self, reason):
		print("Disconnected because %s" %reason)
		sys.stdout.flush()
		self.authed=False

	def onMessageSent(self, jid, messageId):
		reportContent="Jid_MessageID:%s_%s\n"%(jid,messageId)
		fobj=open(self.fileName,"a+")
		fobj.write(reportContent)
		fobj.close()
		self.gotReceipt+=1

	def onMessageReceived(self, messageId, jid, messageContent, timestamp, wantsReceipt, pushName, isBroadCast):
##		print("On messageReceived ")
##		print("TimeStamp:%s"%timestamp)
##		sentTime=datetime.datetime.fromtimestamp(timestamp)
##		print(sentTime)
##		print(sentTime.strftime('%s'))
		self.methodsInterface.call("message_ack", (jid, messageId))
		fileName="%s/tmp/%s_%s_%s"%(self.resourceLocation,timestamp,jid.split("@",1)[0],messageId)
		fobj=open(fileName,"w")
		fobj.write(messageContent)
		fobj.close()
		logfileName="%s/logs/From.log"%self.resourceLocation
		logFileObj=open(logfileName,"a+")
		logFileObj.write("------------------------------------\n")
		now_text = datetime.datetime.now().strftime("%c")
		logFileObj.write("From:%s:\t At: %s\nMessage:\n%s\n"%(jid.split("@",1)[0],now_text,messageContent))
		logFileObj.write("------------------------------------\n")
		logFileObj.close()
		print(os.path.abspath(fobj.name))
		sys.stdout.flush()
