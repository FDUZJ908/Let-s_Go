#!/usr/bin/python
#-*- coding:utf8 -*-
import os
import sys
import json
import time

reload(sys)
sys.setdefaultencoding('utf-8')

DAY_SECONDS=24*60*60
WEEK_SECONDS=7*DAY_SECONDS

cgiPath=os.environ["MiniProg"]+"/cgi-bin/"
resPath=os.environ["miniProgResrcPATH"]
filePath=os.environ["miniProgFilePATH"]
logPath=os.environ["miniProgLogPATH"]
fontPath=os.environ["fonts"]

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
