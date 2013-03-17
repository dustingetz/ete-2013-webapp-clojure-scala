/**
 * Encapsulates the data API.
 * @param root the base path for the urls.
 */
function API(root)
{
	// converts the relative url of the call to an absolute-ish url.
	function url(path) { return root + '/api' + path }
	this.auth = function(username, password)
	{
		return postForm(url('/login'), { 'username': username, 'password': password });
	};
	this.register = function(firstName, lastName, email, username, password1, password2)
	{
		return postForm(url('/register'),
			{
				'firstName': firstName,
				'lastName': lastName,
				'email': email,
				'username': username,
				'password1': password1,
				'password2': password2
			});
	};
	this.whoami = function()
	{
		return getJSON(url('/whoami'));
	};
	this.logout = function()
	{
		return getText(url('/logout'));
	};
	this.listAllSkills = function()
	{
		return getJSON(url('/list-all-skills'));
	};
	this.listUserSkills = function(userId)
	{
		return getJSON(url('/list-user-skills?') + $.param({'userId':userId}));
	};
	this.listUserSkillsPicker = function()
	{
		return getJSON(url('/list-skills-user-picker'));
	};
	this.listOwnedProjects = function()
	{
		return getJSON(url('/list-owned-projects'));
	};
	this.listJoinedProjects = function()
	{
		return getJSON(url('/list-joined-projects'));
	};
	this.listEligibleProjects = function ()
	{
		return getJSON(url('/list-eligible-projects'));
	};
	this.updateUserSkills = function(ids)
	{
		return postJSON(url('/update-user-skills'), ids);
	};
	this.createProject = function(name, skills)
	{
		return postJSON(url('/create-project'), {'name':name,'skills':skills});
	};
	this.deleteProject = function(projectId)
	{
		return getText(url('/delete-project?') + $.param({'projectId':projectId}));
	};
	this.projectAddUser = function (projectId)
	{
		return getText(url('/project-add-member?') + $.param({'projectId':projectId}));
	}
	this.projectRemoveUser = function (projectId)
	{
		return getText(url('/project-remove-member?') + $.param({'projectId':projectId}));
	}
	/**
	 * Gets json from a server.
	 * @param url
	 * @return the ajax promise.
	 */
	function getJSON(url)
	{
		return $.ajax(
		{
			type: "GET",
			url: url,
			contentType: "application/json",
			dataType: "json",
			processData: false
		});
	}
	/**
	 * Gets text from a server.
	 * @param url
	 * @return the ajax promise.
	 */
	function getText(url)
	{
		return $.ajax(
		{
			type: "GET",
			url: url,
			contentType: "text/plain",
			processData: false
		});
	}
	/**
	 * Posts json object to server.
	 * see http://rohanradio.com/blog/2011/02/22/posting-json-with-jquery/
	 * @param url
	 * @param data arbitrary json object put to the post body as a string.
	 * @return the ajax promise (which you'll usually ignore).
	 */
	function postJSON(url, data)
	{
		return $.ajax(
		{
			type: "POST",
			url: url,
			data: JSON.stringify(data),
			contentType: "application/json",
			dataType: "json",
			processData: false
		});
	}
	/**
	 * Posts form data to server encoded as name/value pairs in a json object.
	 * @param url
	 * @param data key value pairs transcribed to form's query string.
	 * @return the ajax promise.
	 */
	function postForm(url, data)
	{
		return $.ajax(
		{
			type: "POST",
			url: url,
			data: data,
			contentType: "application/x-www-form-urlencoded"
		});
	}
}
