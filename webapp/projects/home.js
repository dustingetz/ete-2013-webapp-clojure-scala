
require([
    'libs/assert', 'libs/util', 'libs/templates'
    , 'widgets/topnav'
    , 'widgets/login'
    , 'widgets/register'
    , 'widgets/splash'
    , 'widgets/skills'
    , 'widgets/create-project'
    , 'widgets/projects'
],
    function(a, u, t, topnavF, loginF, registerF
        , splashF, skillsF, createProjectF, projectsF)
    {
        // api is private; only this page knows how to manage the data.
        var api = new API('.');
        var login = loginF.make(t.snippet($, 'login'));
        var register = registerF.make(t.snippet($, 'register'));
        var createProject = createProjectF.make(t.snippet($, 'create-project'));
        // This will contain all the variable HTML we'll show.
        var topnav = topnavF.make(t.snippet($, 'topnav'), 'Artscentre', 'home');
        var $main = t.snippet($, 'main');
        var splash = splashF.make(t.snippet($main, 'splash'));
        var skills = skillsF.make(t.snippet($main, 'skills'), "Your skills...");
        var projects = projectsF.make(t.snippet($main, 'projects'));
        // This will contain all the different widgets we'll show in the main panel.
        var panels = [];

        // this guys sets the cursor during ajax calls.
        $('html').bind('ajaxStart', function()
        {
            $(this).addClass('busy');
        }).bind('ajaxStop', function()
            {
                $(this).removeClass('busy');
            });

        skills.onUpdate(function(ids)
        {
            api.updateUserSkills(ids.ids);
        });
        topnav.clear();
        /**
         * @return {number} an id to use to show the panel, later.
         */
        function addPanel(widget)
        {
            widget.hide();
            return panels.push(widget) - 1;
        }
        /**
         * @param id returned from addPanel.
         */
        function showPanel(id)
        {
            _.each(panels, function(panel)
            {
                panel.hide();
            });
            panels[id].show();
            // blink the well in the hopes that the browser will render it correctly.
            $main.hide();
            $main.show();
        }
        // as we add the panels we collect their ids to be used later for showing and hiding them.
        var SPLASH = addPanel(splash);
        var SKILLS = addPanel(skills);
        var PROJECTS = addPanel(projects);
        showPanel(SPLASH);
        // listen on main menu item
        topnav.onMain(function()
        {
            showPanel(SPLASH);
        });
        topnav.onRegistration(function()
        {
            register.show();
            register.onRegister(function(firstName, lastName, email, username, password1, password2)
            {
                $.when(api.register(firstName, lastName, email, username, password1, password2)).done(function()
                {
                    register.hide();
                }).fail(function(e)
                    {
                        register.natter(e);
                    });
            });
        });
        topnav.onLogin(function()
        {
            login.show();
            login.onLogin(function(username, password)
            {
                $.when(api.auth(username, password)).done(function()
                {
                    login.hide();
                    detectLogin();
                }).fail(function(e)
                    {
                        login.natter(e);
                    });
            });
        });
        topnav.onLogout(function()
        {
            // We're going to assume this works and not even wait for a response.
            api.logout();
            topnav.clear();
            showPanel(SPLASH);
        });
        topnav.onManageAccount(function()
        {
            alert("Maybe some other time, eh?");
        });
        function showSkillsPanel()
        {
            $.when(api.listUserSkillsPicker()).done(function(data)
            {
                skills.addItems(data);
                showPanel(SKILLS);
            }).fail(function()
                {
                    alert('cannot fetch skill list.');
                });
        }
        function showProjectsPanel()
        {
            showPanel(PROJECTS);
        }
        projects.onOwned(function()
        {
            $.when(api.listOwnedProjects()).done(function(data)
            {
                projects.addItems(data);
            }).fail(function(e)
                {
                    alert('cannot fetch owned projects list.\n'+JSON.stringify(e));
                });
        });
        projects.onJoined(function()
        {
            $.when(api.listJoinedProjects()).done(function(data)
            {
                projects.addItems(data);
            }).fail(function(e)
                {
                    alert('cannot fetch joined projects list.\n'+JSON.stringify(e));
                });
        });
        projects.onEligible(function()
        {
            $.when(api.listEligibleProjects()).done(function(data)
            {
                projects.addItems(data);
            }).fail(function(e)
                {
                    alert('cannot fetch eligible projects list.\n'+JSON.stringify(e));
                });
        });
        projects.onCreate(function()
        {
            $.when(api.listAllSkills()).done(function(skills)
            {
                createProject.onSubmit(function(name, skills)
                {
                    $.when(api.createProject(name, skills)).done(function()
                    {
                        createProject.hide();
                    }).fail(function()
                        {
                            createProject.natter(arguments[2]);
                        });
                });
                createProject.show(skills);
            })
        });
        projects.onProjectJoin(function(projectId)
        {
            $.when(api.projectAddUser(projectId)).done(function()
            {
                projects.reload();
            }).fail(function(e)
                {
                    alert('failed to join project.\n'+JSON.stringify(e));
                });
        });
        projects.onProjectLeave(function(projectId)
        {
            $.when(api.projectRemoveUser(projectId)).done(function()
            {
                projects.reload();
            }).fail(function(e)
                {
                    alert('failed to remove user from project.\n'+JSON.stringify(e));
                });
        });
        projects.onProjectDelete(function(projectId)
        {
            $.when(api.deleteProject(projectId)).done(function()
            {
                projects.reload();
            }).fail(function(e)
                {
                    alert('failed to delete project.\n'+JSON.stringify(e));
                });
        });
        // See if we're logged in (i.e. do we haz teh cookie).
        // We do this when the page first loads and after a login attempt.
        // If we find we're logged we update the UI accordingly.
        function detectLogin()
        {
            $.when(api.whoami()).done(function(user)
            {
                // update the UI to reflect the currently logged in user.
                topnav.update(user.firstName + ' ' + user.lastName);
            }).fail(function()
                {
                    topnav.clear();
                });
        }
        detectLogin();
        // Routing
        // This has to go at the end after all the references are defined.
        var AppRouter = Backbone.Router.extend({
            routes:
            {
                'skills': 'skills',
                'projects': 'projects',
                '*actions': 'defaultRoute'
            }
        });
        var router = new AppRouter;
        router.on('route:defaultRoute', function() { showPanel(SPLASH); });
        router.on('route:skills', function() { showSkillsPanel(); });
        router.on('route:projects', function() { showProjectsPanel(); });
        // Necessary step for bookmarkable URL's
        Backbone.history.start();
    });
