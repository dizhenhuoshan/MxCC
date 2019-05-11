set -e
cd "$(dirname "$0")"
nasm -felf64 test.asm
gcc --static -fno-pie -no-pie test.o -o test
