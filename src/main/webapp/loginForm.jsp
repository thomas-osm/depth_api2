<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Login Form</title>
</head>
<body>

<div class="hero-unit">
	<br>
	<h4>Please login before access restricted resources</h4><br>
	<form action="j_security_check" method="post">
		<table>
			<tr>
				<th align="right">your E-Mail:</th>
				<td align="left"><input type="text" name="j_username" size="50"></input></td>
			</tr>
			
			<tr>
				<th align="right">Password:</th>
				<td align="left"><input type="password" name="j_password" size="50"></input></td>
			</tr>
			
			<tr>
				<td align="right"><input type="reset" value="Reset"></input></td>
				<td align="center"><input type="submit" value="Login"></input></td>
			</tr>
		</table>
	</form>
</div>

</body>
</html>