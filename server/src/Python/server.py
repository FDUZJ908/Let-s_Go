#!/usr/bin/python
#-*- coding:utf8 -*-
import os
import sys
import json
import time
import random
import mysql.connector

reload(sys)
sys.setdefaultencoding('utf-8')

DAY_SECONDS=24*60*60
WEEK_SECONDS=7*DAY_SECONDS

class PDBC:
    config={
            "user":"root",
            "password":os.environ["DatabasePassword"],
            "database":"LetsGo"
            }
    conn=None

    def __init__(self):
        self.conn=mysql.connector.connect(**self.config)

    def __del__(self):
        if self.conn!=None:
            self.conn.close()

    def checkUserid(self,userid):
        cur=self.conn.cursor()
        cur.execute("SELECT * FROM user WHERE userid='"+userid+"' LIMIT 1")
        row=cur.fetchone()
        cur.close()
        if row==None:
            return 0
        return 1

pdbc=PDBC()

def getRequestData(flog=None):
    jsonReq_str=""
    try:
        while True:
           jsonReq_str+=raw_input()
    except Exception as e:
        pass
    if flog!=None:
        flog.write(jsonReq_str+"\n")
    return json.loads(jsonReq_str)

def sendResponse(jsonRes,flog=None):
    jsonRes_str=json.dumps(jsonRes)
    print 'Content-type: application/json\n'
    print jsonRes_str
    if flog!=None:
        flog.write(jsonRes_str+"\n")

def writeError(mesg,flog=None):
    jsonRes={"status":"ERROR","message":mesg}
    sendResponse(jsonRes,flog)
    exit()
