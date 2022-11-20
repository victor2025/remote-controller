dir_name=$(pwd)/app/release
app_name=remote-controller
version=$(git describe --tags `git rev-list --tags --max-count=1`)
# rename
ori_path=$dir_name/app-release.apk
new_path=$dir_name/${app_name}-$version.apk
mv -v $ori_path $new_path