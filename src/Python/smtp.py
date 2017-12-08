#!/usr/bin/python
#encoding:utf-8
from server import *
from email import encoders
from email.header import Header
from email.mime.text import MIMEText
from email.utils import parseaddr, formataddr
import smtplib

reload(sys)
sys.setdefaultencoding('utf-8')

def getPassword():
    dirt=os.environ['miniProgResrcPATH']
    password=""
    with open(dirt+"/password","r") as fin:
        password=fin.readline()
    return password

def _format_addr(s):
    name, addr = parseaddr(s)
    return formataddr((Header(name, 'utf-8').encode(), addr.encode('utf-8') if isinstance(addr, unicode) else addr))

jsonReq=getRequestData()

from_addr = "FudanMSN_mp@163.com"
password = getPassword()

to_addr = "lshzy137@163.com"
#to_addr = "qygong99@gmail.com"
smtp_server = "smtp.163.com"

content="描述：\n"+jsonReq['text']+"\n\n联系方式：\n"+jsonReq['contact']

msg = MIMEText(content, 'plain', 'utf-8')
msg['From'] = _format_addr('卿云GO团队<%s>' % from_addr)
msg['To'] = _format_addr(to_addr)
msg['Subject'] = Header(('卿云GO-联系我们-'+jsonReq['subject']), 'utf-8').encode()

response="提交成功！谢谢您的建议！"
try:
    smtp = smtplib.SMTP_SSL(smtp_server,465,timeout=5)
    smtp.ehlo()
    #smtp = smtplib.SMTP(smtp_server,25,timeout=5)
    smtp.login(from_addr,password)  
    smtp.sendmail(from_addr, [to_addr], msg.as_string())
    smtp.quit()
except Exception as e:
    response="提交失败，请稍后重试。"

print "Content-type: text/plain\n\n"+response
