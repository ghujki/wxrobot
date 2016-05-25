# wxrobot 
wxrobot is a opensourced robot for personal wechat account.
 it supplies function to reply the messages for personal account,including personally chat and group chat.
 and interface for customed reply stratage to expand.
 
 for example,you can simply define reply rules like this :
 {"replyType":"Text","match":"hello","replyContent":"Hi ,this is Peter,How are you?"}         
 in this case the robot will reply the replyContent to anyone who says hello to you.
 
 or you can define some much more complicated reply stratage like this
 {"replyType":"Custom","match":"\\d{11}","replyContent":"your.package.path.TelephoneReply"}
 you will make a new class your.package.path.TelephoneReply implement com.wsg.robot.IReply
 then you can defined your customed reply action when any body send messages matches '\d{11}'.
 
 further more ,it involves an online auto-reply interface in .It provides automatic replyment .
 
 
