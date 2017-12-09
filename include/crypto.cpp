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
