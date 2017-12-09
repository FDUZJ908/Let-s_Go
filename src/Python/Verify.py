#!/usr/bin/python
#-*- coding:utf8 -*-
from server import *
from email import encoders
from email.header import Header
from email.mime.text import MIMEText
from email.utils import parseaddr, formataddr
import smtplib

def getEmail():
    return os.environ['EmailAddress']

def getPassword():
    return os.environ['EmailPassword']

def _format_addr(s):
    name, addr = parseaddr(s)
    return formataddr((Header(name, 'utf-8').encode(), addr.encode('utf-8') if isinstance(addr, unicode) else addr))

def main():
    jsonReq=getRequestData()

    if (not jsonReq.has_key("userid")) or (not jsonReq.has_key("type")):
        writeError("Request data error!")

    userid = jsonReq["userid"]
    ty = jsonReq["type"]
    if ty!=1 and ty!=0:
        writeError("Request data error!")
    
    flag = pdbc.checkUserid(userid)
    if ty==0 and flag==1:
        writeError("用户名已经存在!")
    if ty==1 and flag==0:
        writeError("用户名不存在!")

    to_addr = userid
    from_addr = getEmail()
    password = getPassword()
    smtp_server = "smtp.163.com"

    code=str(random.randint(10000000,99999999))
    content="您好:\n\t您本次操作的验证码为："+code+"\n"
    content+="\t请务必妥善保管验证码，不要向他人透漏。\n"
    content+="\t本邮件为系统自动发送，请勿回复。\n"
    content+="\nLet\'s Go\n"

    msg = MIMEText(content, 'plain', 'utf-8')
    msg['From'] = _format_addr('Let\'s Go<%s>' % from_addr)
    msg['To'] = _format_addr(to_addr)
    msg['Subject'] = Header('Let\'s Go验证码', 'utf-8').encode()

    try:
        smtp = smtplib.SMTP_SSL(smtp_server,465,timeout=20)
        smtp.ehlo()
        #smtp = smtplib.SMTP(smtp_server,25,timeout=5)
        smtp.login(from_addr,password)  
        smtp.sendmail(from_addr, [to_addr], msg.as_string())
        smtp.quit()
    except Exception as e:
        writeError("验证码发送失败。")

    jsonRes={"status":"OK","code":code}
    sendResponse(jsonRes)

try:
    main()
except Exception as e:
    writeError(str(e))
