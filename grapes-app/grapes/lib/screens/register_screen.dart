import 'package:flutter/material.dart';
import '../core/theme/app_theme.dart';
import '../services/api_service.dart';

/// Tela de Registro com estilo Pixel Art / Retrô
class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen>
    with SingleTickerProviderStateMixin {
  final _formKey = GlobalKey<FormState>();
  final _nicknameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _apiService = ApiService();

  bool _isLoading = false;
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;
  String? _errorMessage;
  bool _registrationSuccess = false;

  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.easeOut),
    );

    _animationController.forward();
  }

  @override
  void dispose() {
    _nicknameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _animationController.dispose();
    super.dispose();
  }

  Future<void> _handleRegister() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final success = await _apiService.register(
        nickname: _nicknameController.text.trim(),
        email: _emailController.text.trim(),
        password: _passwordController.text,
      );

      if (success && mounted) {
        setState(() => _registrationSuccess = true);

        // Aguarda um pouco e volta para login
        await Future.delayed(const Duration(seconds: 2));
        if (mounted) {
          Navigator.pop(context);
        }
      } else {
        setState(() => _errorMessage = 'Falha no registro. Tente novamente.');
      }
    } catch (e) {
      setState(() => _errorMessage = e.toString());
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppTheme.neonGreen),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          '< VOLTAR',
          style: AppTheme.pixelSmall.copyWith(color: AppTheme.neonGreen),
        ),
        titleSpacing: 0,
      ),
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/background.png'),
            fit: BoxFit.cover,
            colorFilter: ColorFilter.mode(Color(0x99000000), BlendMode.darken),
          ),
        ),
        child: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: FadeTransition(
                opacity: _fadeAnimation,
                child: _registrationSuccess
                    ? _buildSuccessMessage()
                    : _buildRegisterForm(),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSuccessMessage() {
    return Container(
      padding: const EdgeInsets.all(32),
      decoration: AppTheme.greenGlowBorder,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(Icons.check_circle, color: AppTheme.neonGreen, size: 64),
          const SizedBox(height: 24),
          Text(
            'CONTA CRIADA!',
            style: AppTheme.pixelHeading.copyWith(color: AppTheme.neonGreen),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          Text(
            'Redirecionando para login...',
            style: AppTheme.pixelSmall,
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 24),
          SizedBox(
            width: 24,
            height: 24,
            child: CircularProgressIndicator(
              strokeWidth: 3,
              valueColor: AlwaysStoppedAnimation(AppTheme.neonGreen),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRegisterForm() {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          // Header
          _buildHeader(),
          const SizedBox(height: 32),

          // Card de Registro
          _buildRegisterCard(),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return Column(
      children: [
        Image.asset('assets/images/grape_icon.png', width: 64, height: 64),
        const SizedBox(height: 8),
        Text(
          'NOVO JOGADOR',
          style: AppTheme.pixelHeading.copyWith(color: AppTheme.lilac),
        ),
        const SizedBox(height: 4),
        Text(
          'Crie sua conta e comece a jornada!',
          style: AppTheme.pixelSmall,
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildRegisterCard() {
    return Container(
      width: double.infinity,
      constraints: const BoxConstraints(maxWidth: 400),
      decoration: AppTheme.retroBorder,
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Título
          Text(
            '> REGISTRO',
            style: AppTheme.pixelHeading.copyWith(color: AppTheme.neonGreen),
          ),
          const SizedBox(height: 24),

          // Campo Nickname
          TextFormField(
            controller: _nicknameController,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            decoration: AppTheme.inputDecoration(
              'NICKNAME',
              icon: Icons.person,
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Digite um nickname';
              }
              if (value.length < 3) {
                return 'Mínimo 3 caracteres';
              }
              if (value.length > 20) {
                return 'Máximo 20 caracteres';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Campo Email
          TextFormField(
            controller: _emailController,
            keyboardType: TextInputType.emailAddress,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            decoration: AppTheme.inputDecoration('EMAIL', icon: Icons.email),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Digite seu email';
              }
              if (!RegExp(
                r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$',
              ).hasMatch(value)) {
                return 'Email inválido';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Campo Senha
          TextFormField(
            controller: _passwordController,
            obscureText: _obscurePassword,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            decoration: AppTheme.inputDecoration('SENHA', icon: Icons.lock)
                .copyWith(
                  suffixIcon: IconButton(
                    icon: Icon(
                      _obscurePassword
                          ? Icons.visibility
                          : Icons.visibility_off,
                      color: AppTheme.accentPurple,
                    ),
                    onPressed: () =>
                        setState(() => _obscurePassword = !_obscurePassword),
                  ),
                ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Digite uma senha';
              }
              if (value.length < 6) {
                return 'Mínimo 6 caracteres';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Campo Confirmar Senha
          TextFormField(
            controller: _confirmPasswordController,
            obscureText: _obscureConfirmPassword,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            decoration:
                AppTheme.inputDecoration(
                  'CONFIRMAR SENHA',
                  icon: Icons.lock_outline,
                ).copyWith(
                  suffixIcon: IconButton(
                    icon: Icon(
                      _obscureConfirmPassword
                          ? Icons.visibility
                          : Icons.visibility_off,
                      color: AppTheme.accentPurple,
                    ),
                    onPressed: () => setState(
                      () => _obscureConfirmPassword = !_obscureConfirmPassword,
                    ),
                  ),
                ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Confirme sua senha';
              }
              if (value != _passwordController.text) {
                return 'Senhas não coincidem';
              }
              return null;
            },
          ),
          const SizedBox(height: 24),

          // Mensagem de erro
          if (_errorMessage != null) ...[
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppTheme.error.withValues(alpha: 0.2),
                border: Border.all(color: AppTheme.error, width: 2),
              ),
              child: Row(
                children: [
                  Icon(Icons.error_outline, color: AppTheme.error, size: 20),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      _errorMessage!,
                      style: AppTheme.pixelSmall.copyWith(
                        color: AppTheme.error,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
          ],

          // Botão de Registro
          SizedBox(
            height: 56,
            child: ElevatedButton(
              onPressed: _isLoading ? null : _handleRegister,
              style: AppTheme.retroButtonGreen,
              child: _isLoading
                  ? SizedBox(
                      width: 24,
                      height: 24,
                      child: CircularProgressIndicator(
                        strokeWidth: 3,
                        valueColor: AlwaysStoppedAnimation(
                          AppTheme.primaryDark,
                        ),
                      ),
                    )
                  : Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'CRIAR CONTA',
                          style: AppTheme.pixelButton.copyWith(
                            color: AppTheme.primaryDark,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Icon(
                          Icons.person_add,
                          size: 20,
                          color: AppTheme.primaryDark,
                        ),
                      ],
                    ),
            ),
          ),
        ],
      ),
    );
  }
}
