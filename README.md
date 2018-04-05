# Sathi TV

Android app for http://www.sathitv.com.

## Deliverables

Software releases of the app will be downloadable at `https://hockeyapp.net`.
In order to download a test release, you must be a invited/registered user with
a registered test phone.

## Supported devices

Android versions targeted:

1. Android 4.0.3 Ice Cream Sandwich (API level 15)
2. Android 4.1 Jelly Bean (API level 16)
3. Android 4.2 Jelly Bean (API level 17)
4. Android 4.3 Jelly Bean (API level 18)
5. Android 4.4 KitKat (API level 19)
6. Android 5.0 Lollipop (API level 21)
7. Android 5.1 Lollipop (API level 22)

## Codebase

Repository URL: http://git.shirantech.com/samir/sathi-tv

- Master: Main branch containing only versions supposed to be in live production.
Master branch contains only merges from rc branch, after rc branch has gone
through user tests, and is approved as a stable version.

- Release candidate (rc): branch containing all versions deliverable to the
customer, for internal testing purposes. Also can be used for beta testing
with users in the public. Gets merges from develop branch, and any commit
made on rc should only be bug fixes. Any commit on rc must be merged back to
develop too.

- Develop: The development branch. This is where the actual work happens.
Developers are supposed to checkout this branch in order to develop new code.
Commits will happen often and in small chunks.
Feel free to create your own feature branches out of this branch.

## Used libraries

1. https://github.com/koush/ion

## Contributing

TODO: Write contributing

## License

TODO: Write license