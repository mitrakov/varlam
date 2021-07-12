#!/bin/bash

# by Mitrakov Artem, 2017-10-02, GUAP project
# backup script for "varlam" postgresql database
#
# make it executable and run from 'mitrakov' user
# cron cmd: 0 1 * * * /home/mitrakov/backup_varlam.sh
# please create '.pwd_host' file containing password for the remote host and chmod it to 400
# also ensure there is sshpass installed (see www.tecmint.com/sshpass-non-interactive-ssh-login-shell-script-ssh-password)
# be careful! At first run rsync may ask for RSA key fingerprint! So I recommend to run rsync for the first time in manual mode

NOW=$(date +%Y-%m-%d-%H-%M-%S)
PWD_HOST=$(cat /home/mitrakov/.pwd_host)
TMP_DIR=/home/mitrakov/backup_varlam

mkdir -p $TMP_DIR
pg_dump -U mitrakov varlam > $TMP_DIR/varlam_$NOW.sql
sshpass -p $PWD_HOST rsync -avzh --remove-source-files $TMP_DIR mitrakov@winesaps.ru:/home/mitrakov/
