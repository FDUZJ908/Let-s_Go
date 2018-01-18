#include "stdcpp.h"
#include "Util.h"
#include <openssl/sha.h> 

string sha1(const string &str);

string polyhash(const string &str);

int charDecoder(char ch);

int base64Decoder(const string &ins, char *buffer);
