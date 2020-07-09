<template>
    <!DOCTYPE html>
    <!--PLACEHOLDER-->
    <html>
        <head> </head>
        <body>
            <br />
            <br />
            <h1>How to remove the BIOS Supervisor password on a Panasonic ToughBook CF-53</h1>
            <br />
            <p>
                I got my hands on two CF-53 ToughBooks the other day. However, when I wanted to set the date and time
                straight, both of them had a locked off BIOS.
            </p>
            <p>
                I managed to find out the password of one of them so I could reset the supervisor password and have
                what's basically a clean BIOS.
            </p>
            <p>
                Resetting the supervisor password is not as easy as just resetting the CMOS. However, there is a way to
                reset it that doesn't involve you shelling out $30 for some scalper on ebay to send you a new BIOS chip.
            </p>
            <p>
                My way involves the so called CH341A programmer, an ingenious little device for doing work like this.
                The best thing about it is it's only around $3 on eBay and you can use it over and over again.
            </p>
            <br />
            <p>
                There are two ways you can tackle this: Soldering but saving a few bucks or not having to solder but
                spending a couple dollars on a SOP8 programming clip.
            </p>
            <p>
                The first option is my preferred one since you're pretty much guaranteed not to have any connection
                problems.
            </p>
            <br />
            <p>As a first step, you need to take out the metal cover for your wireless card(s).</p>
            <p>
                Then, remove any cards you have currently plugged in. There's not a lot of space in there so you'll be
                thankful for any room you have.
            </p>
            <p>
                I've found it's best to tape the wifi card and that weird four-antenna-thing to the side since the
                antenna cables will want to spring back and that's not a good thing.
            </p>
            <p>
                Next, pull on the plastic sheet that's on top of the board. It's held in with double sided tape but it
                should eventually come out. You might have to use a little bit of force on this.
            </p>
            <p>
                In the next step, remove the CMOS battery. It's this one little connector with both a red and a black
                wire coming out of it.
            </p>
            <br />
            <h2>This is where the two methods diverge. I'll be covering the non-soldering-method first.</h2>
            <br />
            <p>So you got your CH341 programmer and your SOP8 programming clip ready, eh? Alright.</p>
            <p></p>
            <p>
                First, you need to modify your programming clip to fit into the tight space constraints of the
                toughbook.
            </p>
            <p>
                Most programming clips for SOP8 use the same design as the ones for the larger SOP16 format making them
                wider than they need to be.
            </p>
            <p>What you need to do is to cut off the unused two spaces next to the ones that have contacts in them</p>
            <p>
                Now you're ready to put the programming clip into the 25XX-slots of your CH341 and clip pin 1 of your
                programming clip to the chip with the colored dot on it.
            </p>
            <p>
                This dot also marks pin 1 of the chip. You need to align the programming clip with the chip properly,
                doing that WILL take a few tries and you might need to hold it onto the chip with a bit of pressure.
            </p>
            <p>
                Now, go into the software that came with your programmer. Select what kind of chip is in your ToughBook
                (it's an MX25L6406E by MXIC in my case)
            </p>
            <p>
                Read the contents off the chip. This is to provide a backup as well as to test if the programming clip
                is connected to your chip properly which can be a real pain in the rear.
            </p>
            <p>It will take a while since it's an 8MB chip but once it's finished scroll through the data.</p>
            <p>
                If there's nothing but blank spaces or if there's only blank spaces from one point til the end of the
                data chances are your programming clip has gotten disconnected in the process of reading the data.
            </p>
            <p>
                Verifying the code will basically do the same thing except if you have severe connection problems and
                your programmer can't read any data off the chip it'll still verify fine.
            </p>
            <p>
                If it seems to have read the data correctly go ahead and save the file. Now go and click erase in the
                program. This will blank out the chip so that you can write new data on it
            </p>
            <p>
                I have had problems with the CH341 spitting out an error if you didn't read the chip another time just
                to verify it's blank so go ahead and do that.
            </p>
            <p>
                Now, open the BIOS file which I linked down below somewhere and click program. If the program spits out
                an error, erase the chip and try again.
            </p>
            <p>
                If it's written the new BIOS to the chip successfully it'll spit out something like "chip and buffer
                same". This means you have successfully written the new non-password-protected BIOS onto the chip.
            </p>
            <p>
                You can now put the CMOS battery back in, screw in your card(s) and boot up the laptop. It should no
                longer have a supervisor password set for its BIOS.
            </p>
            <br />
            <br />
            <h2>
                This method is my preferred one since you don't have to fumble around with a programming clip like a
                fucking moron.
            </h2>
            <br />
            <br />
            <p>
                For this one you're going to need a hot air soldering station, a regular soldering station, a CH341
                programmer as well as a bit of solder.
            </p>
            <p>
                If you're thinking about taking the board out to do this, well, don't even fucking try. It's a nightmare
                to do and you will regret every single part of it.
            </p>
            <p>First, set your hot air station to low air; you don't want to melt the WiFi card connector.</p>
            <p>
                Now, desolder the chip off the board. You may want to blow air away from the WiFi card slot so that it
                doesn't melt.
            </p>
            <p>Now that you've successfully desoldered the chip, you're ready to program the new BIOS onto it.</p>
            <p>
                Your CH341 programmer should've come with a little adapter board allowing you to plug in SMD components.
                Go ahead and solder the chip onto that board and plug it into your CH341 in the 25XX position.
            </p>
            <p>
                You can now open the program that came with your CH341. Go ahead and select the chip that's inside your
                laptop (Mine's an MX25L6406E by MXIC)
            </p>
            <p>
                Now read the data off the chip. This is to provide a backup if something fails. When it's finished, look
                through the data. If it's completely blank you're probably having connection problems. If that's not the
                case, go ahead and verify the code.
            </p>
            <p>
                If it seems to have read the data correctly go ahead and save the file. Now go and click erase in the
                program. This will blank out the chip so that you can write new data on it
            </p>
            <p>
                I have had problems with the CH341 spitting out an error if you didn't read the chip another time just
                to verify it's blank so go ahead and do that.
            </p>
            <p>
                Now, open the BIOS file which I linked down below somewhere and click program. If the program spits out
                an error, erase the chip and try again.
            </p>
            <p>
                If it's written the new BIOS to the chip successfully it'll spit out something like "chip and buffer
                same". This means you have successfully written the new non-password-protected BIOS onto the chip.
            </p>
            <p>
                You're now ready to solder the chip back in. There's a little marker showing you pad #1. This will
                correspond to the little colored dot on your chip. Align the pins to the pads and solder it back in.
            </p>
            <p>This may be the pedant speaking out of me but go ahead and clean up the flux residue, will ya?</p>
            <p>
                You can now put the CMOS battery back in, screw in your card(s) and boot up the laptop. It should no
                longer have a supervisor password set for its BIOS.
            </p>
            <br />
        </body>
    </html>
</template>

<script>
export default {
    name: 'Help',
}
</script>

<style scoped></style>
