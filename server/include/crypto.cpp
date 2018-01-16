#include "crypto.h"

string sha1(const string &str)
{
    unsigned char digest[SHA_DIGEST_LENGTH];
    char res[2*SHA_DIGEST_LENGTH+1];
    SHA1((unsigned char *)str.c_str(), str.size(), (unsigned char *)digest);
    for(int i=0;i<SHA_DIGEST_LENGTH;i++)
        sprintf(res+2*i, "%02x", (unsigned int)digest[i]);
    res[2*SHA_DIGEST_LENGTH]='\0';
    return res;
}

#define MOD 127

string polyhash(const string &str)
{
    int seed=atoi(getenv("HASHSEED"));
    int n=str.size();
    char res[n+1];
    for(int i=0,x=1;i<n;i++,x=(x*seed)%MOD)
        res[i]=(int(str[i])*x)%MOD+1;
    res[n]='\0';
    return res;
}

int charDecoder(char ch)
{
    if('A'<=ch && ch<='Z') return ch-'A';
    if('a'<=ch && ch<='z') return 26+ch-'a';
    if('0'<=ch && ch<='9') return 52+ch-'0';
    if(ch=='+') return 62;
    if(ch=='-') return 63;
    return 0;
}

int base64Decoder(const char *s, char *buffer)
{
    int n=strlen(s),m=0;
    for(int i=0;i<n;i+=4)
    {
        int x=0;
        for(int j=0;j<4;j++)
            x=x | (charDecoder(s[i+j])<<((3-j)*6));
        buffer[m+2]=(x&0xFF); x>>=8;
        buffer[m+1]=(x&0xFF); x>>=8;
        buffer[m]=(x&0xFF); x>>=8;
        m+=3;
    }
    if(s[--n]=='=') m--;
    if(s[--n]=='=') m--;
    buffer[m]='\0';
    return m;
}
